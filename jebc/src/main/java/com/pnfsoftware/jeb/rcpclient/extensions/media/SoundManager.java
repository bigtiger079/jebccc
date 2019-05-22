package com.pnfsoftware.jeb.rcpclient.extensions.media;

import com.pnfsoftware.jeb.util.logging.GlobalLog;
import com.pnfsoftware.jeb.util.logging.ILogger;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.sound.sampled.AudioFileFormat;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;
import javax.sound.sampled.TargetDataLine;
import javax.sound.sampled.UnsupportedAudioFileException;

public class SoundManager implements LineListener {
    private static final ILogger logger = GlobalLog.getLogger(SoundManager.class);
    public static final int STOPPED = 0;
    public static final int RECORDING = 1;
    public static final int PLAYING = 2;
    final float sampleRate = 8000.0F;
    final int sampleSizeInBits = 8;
    final int channels = 1;
    final boolean signed = true;
    final boolean bigEndian = true;
    List<IStateChangedListener> listeners = new ArrayList<>();
    int state = 0;
    ByteArrayOutputStream datastream = new ByteArrayOutputStream();
    Thread captureThread = null;
    Thread playThread = null;

    public SoundManager() {
    }

    public SoundManager(InputStream in) throws UnsupportedAudioFileException, IOException {
        load(in);
    }

    public void load(InputStream in) throws UnsupportedAudioFileException, IOException {
        AudioInputStream ais = AudioSystem.getAudioInputStream((in instanceof BufferedInputStream) ? in : new BufferedInputStream(in));
        int bytesPerFrame = ais.getFormat().getFrameSize();
        if (bytesPerFrame == -1) {
            bytesPerFrame = 1;
        }
        this.datastream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8000 * bytesPerFrame];
        int cnt;
        while ((cnt = ais.read(buffer)) != -1) {
            this.datastream.write(buffer, 0, cnt);
        }
        ais.close();
    }

    public boolean dump(OutputStream out) throws IOException {
        if (this.state != 0) {
            return false;
        }
        byte[] data = this.datastream.toByteArray();
        InputStream input = new ByteArrayInputStream(data);
        AudioFormat format = new AudioFormat(8000.0F, 8, 1, true, true);
        AudioInputStream ais = new AudioInputStream(input, format, data.length / format.getFrameSize());
        AudioFileFormat.Type type = AudioFileFormat.Type.AIFF;
        if (!AudioSystem.isFileTypeSupported(type, ais)) {
            AudioFileFormat.Type[] types = AudioSystem.getAudioFileTypes(ais);
            if (types.length == 0) {
                ais.close();
                return false;
            }
            type = types[0];
        }
        AudioSystem.write(ais, type, out);
        ais.close();
        return true;
    }

    public void addStateChangedListener(IStateChangedListener listener) {
        this.listeners.add(listener);
    }

    public void removeStateChangedListener(IStateChangedListener listener) {
        this.listeners.remove(listener);
    }

    public int getState() {
        return this.state;
    }

    private void setState(int new_state) {
        if (this.state == new_state) {
            return;
        }
        int old_state = this.state;
        this.state = new_state;
        for (IStateChangedListener listener : this.listeners) {
            listener.stateChanged(old_state, new_state);
        }
    }

    public boolean hasAudioData() {
        return this.datastream.size() > 0;
    }

    public boolean waitForState(int wanted_state, long max_wait_ms) {
        long start = System.currentTimeMillis();
        while ((this.state != wanted_state) && ((max_wait_ms < 0L) || (System.currentTimeMillis() - start < max_wait_ms))) {
            try {
                Thread.sleep(200L);
            } catch (InterruptedException e) {
            }
        }
        return this.state == wanted_state;
    }

    public boolean reset() {
        if (this.state != 0) {
            logger.i("reset() FAILED");
            return false;
        }
        this.datastream = new ByteArrayOutputStream();
        return true;
    }

    public boolean stop() throws Exception {
        if (this.state == 0) {
            return true;
        }
        if (this.state == 1) {
            return stopRecording();
        }
        if (this.state == 2) {
            return stopPlaying();
        }
        throw new RuntimeException();
    }

    public boolean record() throws Exception {
        if (this.state != 0) {
            logger.i("record() FAILED");
            return false;
        }
        setState(1);
        final ByteArrayOutputStream sample = new ByteArrayOutputStream();
        final AudioFormat format = new AudioFormat(8000.0F, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(TargetDataLine.class, format);
        final TargetDataLine line = (TargetDataLine) AudioSystem.getLine(info);
        line.open(format);
        line.start();
        Runnable runner = new Runnable() {
            int bufferSize = (int) (format.getSampleRate() * format.getFrameSize());
            byte[] buffer = new byte[this.bufferSize];

            public void run() {
                try {
                    while (SoundManager.this.state == 1) {
                        int count = line.read(this.buffer, 0, this.buffer.length);
                        if (count > 0) {
                            sample.write(this.buffer, 0, count);
                        }
                    }
                    line.stop();
                    line.close();
                    SoundManager.this.datastream.write(sample.toByteArray());
                } catch (Exception e) {
                    SoundManager.this.setState(0);
                }
            }
        };
        this.captureThread = new Thread(runner);
        this.captureThread.start();
        return true;
    }

    public boolean stopRecording() throws Exception {
        if (this.state != 1) {
            logger.i("stopRecording() FAILED");
            return false;
        }
        setState(0);
        this.captureThread.join();
        this.captureThread = null;
        return true;
    }

    public boolean play() throws LineUnavailableException {
        if ((this.state != 0) || (this.datastream == null)) {
            logger.i("play() FAILED");
            return false;
        }
        setState(2);
        byte[] data = this.datastream.toByteArray();
        InputStream input = new ByteArrayInputStream(data);
        final AudioFormat format = new AudioFormat(8000.0F, 8, 1, true, true);
        DataLine.Info info = new DataLine.Info(SourceDataLine.class, format);
        final SourceDataLine line = (SourceDataLine) AudioSystem.getLine(info);
        final AudioInputStream ais = new AudioInputStream(input, format, data.length / format.getFrameSize());
        line.open(format);
        line.start();
        Runnable runner = new Runnable() {
            int bufferSize = (int) (format.getSampleRate() * format.getFrameSize());
            byte[] buffer = new byte[this.bufferSize];

            public void run() {
                try {
                    int count;
                    while ((SoundManager.this.state == 2) && ((count = ais.read(this.buffer, 0, this.buffer.length)) != -1)) {
                        if (count > 0) {
                            line.write(this.buffer, 0, count);
                        }
                    }
                    line.drain();
                    line.close();
                    SoundManager.this.setState(0);
                } catch (IOException e) {
                    SoundManager.this.setState(0);
                }
            }
        };
        this.playThread = new Thread(runner);
        this.playThread.start();
        return true;
    }

    public boolean stopPlaying() throws Exception {
        if (this.state != 2) {
            logger.i("stopPlaying() FAILED");
            return false;
        }
        setState(0);
        this.playThread.join();
        this.playThread = null;
        return true;
    }

    public void update(LineEvent e) {
        logger.i("[+] Event: %s", e);
    }
}


