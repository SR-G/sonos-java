package org.tensin.sonos.tts.renderer.impl;

import java.io.InputStream;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;

import org.tensin.sonos.SonosException;
import org.tensin.sonos.tts.builder.VoiceBuilderOutput;
import org.tensin.sonos.tts.renderer.ISpeechRenderer;
import org.tensin.sonos.tts.renderer.RendererType;

/**
 * The Class SpeechRendererLocal.
 */
public class SpeechRendererLocal extends AbstractSpeechRenderer implements ISpeechRenderer {

    /* (non-Javadoc)
     * @see org.tensin.sonos.tts.renderer.ISpeechRenderer#getType()
     */
    @Override
    public RendererType getType() {
        return RendererType.LOCAL;
    }
    
    /** The buffer size. */
    private static final int BUFFER_SIZE = 128 * 1024;
    
    /* (non-Javadoc)
     * @see org.tensin.sonos.tts.renderer.ISpeechRenderer#render(org.tensin.sonos.tts.builder.VoiceBuilderOutput, java.lang.String[])
     */
    @Override
    public void render(final VoiceBuilderOutput voiceBuilderOutput, final String... zones) throws SonosException {
        SourceDataLine sourceLine = null;        
        try {
            final AudioInputStream audioStream = AudioSystem.getAudioInputStream(voiceBuilderOutput.getInputStream());
            AudioFormat audioFormat = audioStream.getFormat();
            DataLine.Info info = new DataLine.Info(SourceDataLine.class, audioFormat);
            sourceLine = (SourceDataLine) AudioSystem.getLine(info);
            sourceLine.open(audioFormat);
            sourceLine.start();
            int nBytesRead = 0;
            byte[] abData = new byte[BUFFER_SIZE];
            while (nBytesRead != -1) {
                nBytesRead = audioStream.read(abData, 0, abData.length);
                if (nBytesRead >= 0) {
                    @SuppressWarnings("unused")
                    int nBytesWritten = sourceLine.write(abData, 0, nBytesRead);
                }
            }
            sourceLine.drain();
        } catch (LineUnavailableException e) {
            throw new SonosException("Can't render stream locally", e);
        } catch (Exception e) {
            throw new SonosException("Can't render stream locally", e);
        } finally {
            if (sourceLine != null) {
                sourceLine.close();
            }
        }
        
        
//        try {
//            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(speechInputStream);
//            Clip clip;
//            clip = AudioSystem.getClip();
//            clip.open(audioInputStream );
//            clip.start();
//        } catch (LineUnavailableException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        } catch (UnsupportedAudioFileException e) {
//            e.printStackTrace();
//        }
    }
}