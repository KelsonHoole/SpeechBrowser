package cn.hukecn.speechbrowser.speech;
/**
 * Copyright (C) 2015 Baidu, Inc. All Rights Reserved.
 */

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import cn.hukecn.speechbrowser.util.ToastUtil;

import com.baidu.tts.answer.auth.AuthInfo;
import com.baidu.tts.client.SpeechError;
import com.baidu.tts.client.SpeechSynthesizeBag;
import com.baidu.tts.client.SpeechSynthesizer;
import com.baidu.tts.client.SpeechSynthesizerListener;
import com.baidu.tts.client.TtsMode;

/**
 * @author liweigao 2015年9月15日
 */
public class BdTts implements SpeechSynthesizerListener{
    private SpeechSynthesizer mSpeechSynthesizer;
    private String mSampleDirPath;
    private static final String SAMPLE_DIR_NAME = "baiduTTS";
    private static final String SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female.dat";
    private static final String SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male.dat";
    private static final String TEXT_MODEL_NAME = "bd_etts_text.dat";
    private static final String LICENSE_FILE_NAME = "temp_license";
    private static final String ENGLISH_SPEECH_FEMALE_MODEL_NAME = "bd_etts_speech_female_en.dat";
    private static final String ENGLISH_SPEECH_MALE_MODEL_NAME = "bd_etts_speech_male_en.dat";
    private static final String ENGLISH_TEXT_MODEL_NAME = "bd_etts_text_en.dat";
    private Context context;
    private final int N = 50;
    private SpeechCallback listener = null;
    private final int ONERROR = 1;
    private final int ONSPEECHFINISH = 2;
    private final int ONSPEECHPROGRESSCHANGED = 3;
    private final int ONSPEECHSTART = 4;
    
    Handler handler = new Handler(){
    	public void handleMessage(android.os.Message msg) {
    		switch (msg.what) {
			case ONSPEECHSTART:
				listener.onSpeechStart((String)msg.obj);
				break;
			case ONSPEECHPROGRESSCHANGED:
				listener.onSpeechProgressChanged((String)msg.obj, msg.arg1);
				break;
			case ONSPEECHFINISH:
				listener.onSpeechFinish((String)msg.obj);
				break;
			case ONERROR:
				Bundle bundle = msg.getData();
				listener.onError(bundle.getString("arg0"), (SpeechError)msg.obj);
			default:
				break;
			}
    	};
    };
    
    public BdTts(Context context,SpeechCallback listener) {
		// TODO Auto-generated constructor stub
    	this.context = context;
    	this.listener = listener;
    	initialEnv();
        initialTts();

	}
    
    public void speak(String content)
    {
    	if(content.length() == 0)
    	{
    		mSpeechSynthesizer.speak("语音合成失败");
    		return;
    	}
    	
    	if(content.length() > N)
    	{
    		List<SpeechSynthesizeBag> list = splitStringToList(content);
    		mSpeechSynthesizer.batchSpeak(list);
    	}else
    		mSpeechSynthesizer.speak(content);
    }
    public void stop()
    {
    	mSpeechSynthesizer.stop();
    }
    
    public void pause()
    {
    	mSpeechSynthesizer.pause();
    }
    
    public void resume()
    {
    	mSpeechSynthesizer.resume();
    }
    
    private List<SpeechSynthesizeBag> splitStringToList(String content) {
		// TODO Auto-generated method stub
    	List<SpeechSynthesizeBag> list = new ArrayList<SpeechSynthesizeBag>();
    	int offset = 0;
    	int total = content.length() / N;
    	//长度=N的
    	for(int i = 0;i < total;i++)
    	{
    		String temp = content.substring(offset, offset + N);
			SpeechSynthesizeBag bean = new SpeechSynthesizeBag();
			bean.setText(temp);
    		list.add(bean);
			offset += N;
    	}
    	//剩下的不够长度N的
    	if(content.length() > offset)
    	{
    		String temp = content.substring(offset, content.length());
    		SpeechSynthesizeBag bean = new SpeechSynthesizeBag();
			bean.setText(temp);
    		list.add(bean);
    	}
    	return list;
	}

	/*
     * @param savedInstanceState
     */
   
    private void initialEnv() {
        if (mSampleDirPath == null) {
            String sdcardPath = Environment.getExternalStorageDirectory().toString();
            mSampleDirPath = sdcardPath + "/" + SAMPLE_DIR_NAME;
        }
        makeDir(mSampleDirPath);
        copyFromAssetsToSdcard(false, SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/" + SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, TEXT_MODEL_NAME, mSampleDirPath + "/" + TEXT_MODEL_NAME);
        copyFromAssetsToSdcard(false, LICENSE_FILE_NAME, mSampleDirPath + "/" + LICENSE_FILE_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_SPEECH_MALE_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_SPEECH_MALE_MODEL_NAME);
        copyFromAssetsToSdcard(false, "english/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath + "/"
                + ENGLISH_TEXT_MODEL_NAME);
    }

    private void makeDir(String dirPath) {
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    /**
     * 将sample工程需要的资源文件拷贝到SD卡中使用（授权文件为临时授权文件，请注册正式授权）
     * 
     * @param isCover 是否覆盖已存在的目标文件
     * @param source
     * @param dest
     */
    private void copyFromAssetsToSdcard(boolean isCover, String source, String dest) {
        File file = new File(dest);
        if (isCover || (!isCover && !file.exists())) {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                is = context.getResources().getAssets().open(source);
                String path = dest;
                fos = new FileOutputStream(path);
                byte[] buffer = new byte[1024];
                int size = 0;
                while ((size = is.read(buffer, 0, 1024)) >= 0) {
                    fos.write(buffer, 0, size);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    if (is != null) {
                        is.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private void initialTts() {
        this.mSpeechSynthesizer = SpeechSynthesizer.getInstance();
        this.mSpeechSynthesizer.setContext(context);
        this.mSpeechSynthesizer.setSpeechSynthesizerListener(this);
        // 文本模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_TEXT_MODEL_FILE, mSampleDirPath + "/"
                + TEXT_MODEL_NAME);
        // 声学模型文件路径 (离线引擎使用)
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_SPEECH_MODEL_FILE, mSampleDirPath + "/"
                + SPEECH_FEMALE_MODEL_NAME);
        // 本地授权文件路径,如未设置将使用默认路径.设置临时授权文件路径，LICENCE_FILE_NAME请替换成临时授权文件的实际路径，仅在使用临时license文件时需要进行设置，如果在[应用管理]中开通了离线授权，不需要设置该参数，建议将该行代码删除（离线引擎）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_TTS_LICENCE_FILE, mSampleDirPath + "/"
                + LICENSE_FILE_NAME);
        // 请替换为语音开发者平台上注册应用得到的App ID (离线授权)
        this.mSpeechSynthesizer.setAppId("7785404");
        // 请替换为语音开发者平台注册应用得到的apikey和secretkey (在线授权)
        this.mSpeechSynthesizer.setApiKey("lwKnE7mAureerM3sqoq7WDqL", "5fZvQ5GX3PbwZgO0OmZmLcV339TEZdx0");
        // 发音人（在线引擎），可用参数为0,1,2,3。。。（服务器端会动态增加，各值含义参考文档，以文档说明为准。0--普通女声，1--普通男声，2--特别男声，3--情感男声。。。）
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_SPEAKER, "0");
        // 设置Mix模式的合成策略
        this.mSpeechSynthesizer.setParam(SpeechSynthesizer.PARAM_MIX_MODE, SpeechSynthesizer.MIX_MODE_DEFAULT);
        // 授权检测接口(可以不使用，只是验证授权是否成功)
        AuthInfo authInfo = this.mSpeechSynthesizer.auth(TtsMode.MIX);
        if (authInfo.isSuccess()) {
        } else {
            String errorMsg = authInfo.getTtsError().getDetailMessage();
        }
        // 初始化tts
        mSpeechSynthesizer.initTts(TtsMode.MIX);
        // 加载离线英文资源（提供离线英文合成功能）
        int result =
                mSpeechSynthesizer.loadEnglishModel(mSampleDirPath + "/" + ENGLISH_TEXT_MODEL_NAME, mSampleDirPath
                        + "/" + ENGLISH_SPEECH_FEMALE_MODEL_NAME);
    }
    
    
    public interface SpeechCallback{
    	public void onError(String arg0, SpeechError arg1);
    	public void onSpeechFinish(String arg0);
    	public void onSpeechProgressChanged(String arg0, int arg1);
    	public void onSpeechStart(String arg0);
//    	public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2);
//    	public void onSynthesizeFinish(String arg0);
//    	public void onSynthesizeStart(String arg0);
    }



	@Override
	public void onError(String arg0, SpeechError arg1) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();
		Bundle bundle = new Bundle();
		bundle.putString("arg0", arg0);
		msg.obj = arg1;
		msg.what = ONERROR;
		msg.setData(bundle);
		handler.sendMessage(msg);
	}

	@Override
	public void onSpeechFinish(String arg0) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();
		msg.what = ONSPEECHFINISH;
		msg.obj = arg0;
		handler.sendMessage(msg);
	}

	@Override
	public void onSpeechProgressChanged(String arg0, int arg1) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();
		msg.what = ONSPEECHPROGRESSCHANGED;
		msg.arg1 = arg1;
		msg.obj = arg0;
		handler.sendMessage(msg);
	}

	@Override
	public void onSpeechStart(String arg0) {
		// TODO Auto-generated method stub
		Message msg = handler.obtainMessage();
		msg.what = ONSPEECHSTART;
		msg.obj = arg0;
		handler.sendMessage(msg);

	}

	@Override
	public void onSynthesizeDataArrived(String arg0, byte[] arg1, int arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSynthesizeFinish(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onSynthesizeStart(String arg0) {
		// TODO Auto-generated method stub
		
	}
}

	
