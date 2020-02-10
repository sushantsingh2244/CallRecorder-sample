package com.sushi.CallRecorder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.MediaRecorder;
import android.os.Environment;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.Date;

public class CallReceiver extends BroadcastReceiver {
    private MediaRecorder rec;
    private boolean recoderstarted;
    private String number;

    @Override
    public void onReceive(Context context, Intent intent) {
        Date date = new Date();
        String stringDate = DateFormat.getDateTimeInstance().format(date);
        File file = Environment.getExternalStorageDirectory();
        File dir = new File(file.getAbsolutePath() + "/CallRecording/");
        if (!dir.exists())
            dir.mkdir();
        if (intent.getAction() != null) {
            if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL)) {
                number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
            } else {
                String stateStr = intent.getExtras().getString(TelephonyManager.EXTRA_STATE);
                number = intent.getExtras().getString(TelephonyManager.EXTRA_INCOMING_NUMBER);
                Log.i("tag", "Incoming number : " + number);
                rec = new MediaRecorder();
                rec.setAudioSource(MediaRecorder.AudioSource.MIC);
                rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                rec.setOutputFile(dir + "/" + number + "_" + stringDate + "_rec.mp3");
                rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                int state = 0;
                if (stateStr.equals(TelephonyManager.EXTRA_STATE_IDLE)) {
                    state = TelephonyManager.CALL_STATE_IDLE;
                    try {
                        if (rec == null) {
                            rec.stop();
                            rec.reset();
                            rec.release();
                            recoderstarted = false;
                            rec = null;
                        } else {
                            rec.stop();
                            rec.reset();
                            rec.release();
                            recoderstarted = false;
                            rec = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_OFFHOOK)) {
                    state = TelephonyManager.CALL_STATE_OFFHOOK;
                    try {
                        rec = new MediaRecorder();
                        rec.setAudioSource(MediaRecorder.AudioSource.VOICE_COMMUNICATION);
                        rec.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                        rec.setOutputFile(dir + "/" + number + "_" + stringDate + "_rec.mp3");
                        rec.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                        rec.prepare();
                        rec.start();
                        recoderstarted = true;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else if (stateStr.equals(TelephonyManager.EXTRA_STATE_RINGING)) {
                    state = TelephonyManager.CALL_STATE_RINGING;
                }
            }
        }
    }
}
