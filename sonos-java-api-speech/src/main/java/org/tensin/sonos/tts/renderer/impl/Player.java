package org.tensin.sonos.tts.renderer.impl;

import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.kc7bfi.jflac.FLACDecoder;
import org.kc7bfi.jflac.PCMProcessor;
import org.kc7bfi.jflac.metadata.StreamInfo;
import org.kc7bfi.jflac.util.ByteData;

public class Player implements PCMProcessor {
    private AudioFormat fmt;
    private DataLine.Info info;
    private SourceDataLine line;
    private List<LineListener> listeners = new ArrayList<LineListener>();

    public void addListener(LineListener listener) {
        listeners.add(listener);
    }

    /**
     * Decode and play an input FLAC file.
     * 
     * @param inFileName
     *            The input FLAC file name
     * @throws IOException
     *             Thrown if error reading file
     * @throws LineUnavailableException
     *             Thrown if error playing file
     */
    public void decode(InputStream is) throws IOException, LineUnavailableException {
        final FLACDecoder decoder = new FLACDecoder(is);
        decoder.addPCMProcessor(this);
        try {
            decoder.decode();
        } catch (EOFException e) {
        }

        line.drain();
        line.close();

        // We're going to clear out the list of listeners as well, so that everytime through
        // things are basically at the same starting point.
        listeners.clear();
    }

    /**
     * Process the StreamInfo block.
     * 
     * @param streamInfo
     *            the StreamInfo block
     * @see org.kc7bfi.jflac.PCMProcessor#processStreamInfo(org.kc7bfi.jflac.metadata.StreamInfo)
     */
    public void processStreamInfo(StreamInfo streamInfo) {
        try {
            fmt = streamInfo.getAudioFormat();
            info = new DataLine.Info(SourceDataLine.class, fmt, AudioSystem.NOT_SPECIFIED);
            line = (SourceDataLine) AudioSystem.getLine(info);

            // Add the listeners to the line at this point, it's the only
            // way to get the events triggered.
            int size = listeners.size();
            for (int index = 0; index < size; index++)
                line.addLineListener((LineListener) listeners.get(index));

            line.open(fmt, AudioSystem.NOT_SPECIFIED);
            line.start();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    /**
     * Process the decoded PCM bytes.
     * 
     * @param pcm
     *            The decoded PCM data
     * @see org.kc7bfi.jflac.PCMProcessor#processPCM(org.kc7bfi.jflac.util.ByteSpace)
     */
    public void processPCM(ByteData pcm) {
        line.write(pcm.getData(), 0, pcm.getLen());
    }

    /**
     * Removes the listener.
     *
     * @param listener the listener
     */
    public void removeListener(LineListener listener) {
        listeners.remove(listener);
    }
}