package de.hsfl.soundgen.soundgenerator;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    // Frequency constants in Hertz
    private final int[] freq = {264, 330, 396, 440, 495};
    private final int samplingRate = 11025;
    private int duration = 3;
    private final int elementNumber = duration * samplingRate;
    private final double [] elements = new double[elementNumber];
    private final int freqHigher = 300;
    public static final String TAG = "JS.SR";
    byte [] byteSound = new byte[2* elementNumber];
    final double tVec[] = {0, duration*0.3,  duration*0.8, duration};
    final double ampVec[] = {0, 1, 0.7, 0};




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void playTone(View v){
        Button btnTone = (Button) v;
        Log.v(TAG, "playTone: " + btnTone.getText());
        int toneIndex;
        switch (v.getId()){
            case R.id.button1:
                toneIndex = 0;
                break;
            case R.id.button2:
                toneIndex = 1;
                break;
            case R.id.button3:
                toneIndex = 2;
                break;
            case R.id.button4:
                toneIndex = 3;
                break;
            case R.id.button5:
                toneIndex = 4;
                break;
            default:
                toneIndex = -50;
        }

        playSound(convertToBytes(editSin(generateSin(toneIndex))));
    }

    private double[] editSin(double[] elements) {
        double[] envelope = funcGeneratePiecwiseLin(tVec, ampVec, elements.length);

        for (int  j= 0; j< elements.length; j++){
            elements[j]= elements[j]*envelope[j];
        }
        return elements;
    }

    private double[] funcGeneratePiecwiseLin(double[] tVec, double[] ampVec, int count) {
        double[] envelope = new double[count];
        for(int i = 0; i <envelope.length; i++){
            envelope[i]= 1;
        }
        double startFadeStart= 0;
        double startFade = duration* 0.3;


        for(int i = 0; i <envelope.length*startFade; i++){
            envelope[i]= i / (envelope.length*startFade);
        }

        int endFadeStart= (int)(count*0.5);
        double endFade = count;

        for(int i = endFadeStart; i <endFade; i++){
            envelope[i]= (endFade - i) / (envelope.length*startFade);
        }




        /*int nTVec = (int) (tVec * count);
        segmentCount = tVec.length;
        for (int k = 1; k< tVec.length; k++){
            double inc = (ampVec[k] - ampVec[k-1]) /
        }*/
       /* for (int i = 0; i < count; i++) {
            envelope[i] = count / i;
        }*/
        return envelope;
    }



    private double[] generateSin(int toneIndex) {
        Log.v(TAG, "generateSin");

        for (int i = 0; i < elementNumber; ++i) {
            double tVecAtElement = (samplingRate/freq[toneIndex]);

            elements[i] = Math.sin(2 * Math.PI * i /tVecAtElement);
        }
    return elements;
    }

    public byte[] convertToBytes(double [] elements){
        Log.v(TAG, "convertToBytes");
        // assumes the sample buffer is normalised.
        int idx = 0;
        for (final double dVal : elements) {
            // scale to maximum amplitude
            final short val = (short) ((dVal * 32767));
            // in 16 bit wav PCM, first byte is the low order byte
            byteSound[idx++] = (byte) (val & 0x00ff);
            byteSound[idx++] = (byte) ((val & 0xff00) >>> 8);

        }
        return byteSound;
    }


    void playSound(byte [] byteSound){
        Log.v(TAG, "playSound");
        final AudioTrack at = new AudioTrack(AudioManager.STREAM_MUSIC,
                samplingRate, AudioFormat.CHANNEL_OUT_MONO,
                AudioFormat.ENCODING_PCM_16BIT, byteSound.length,
                AudioTrack.MODE_STATIC);
        at.write(byteSound, 0, byteSound.length);
        at.play();
    }
}