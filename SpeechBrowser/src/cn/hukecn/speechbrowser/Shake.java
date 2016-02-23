package cn.hukecn.speechbrowser;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class Shake{
	
	private static SensorManager mSensorManager;
	private static ShakeListener mlistener = null;
	
	private Shake(Context context,ShakeListener listener) {}
	
	public static void registerListener(Context context,ShakeListener listener){
		//获取传感器管理服务  
        mSensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);  
        //震动服务
      //加速度传感器  
		mSensorManager.registerListener(seListener,  
				mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),  
		//还有SENSOR_DELAY_UI、SENSOR_DELAY_FASTEST、SENSOR_DELAY_GAME等，  
		//根据不同应用，需要的反应速率不同，具体根据实际情况设定  
				SensorManager.SENSOR_DELAY_NORMAL);
		
		mlistener = listener;
	}
	
	public static void removeListener(){
		mSensorManager.unregisterListener(seListener);
	}
	
	
	
	public interface ShakeListener{
		public void onShake();	
		}


	public static SensorEventListener seListener = new SensorEventListener() {
		
		@Override
		public void onSensorChanged(SensorEvent event) {
			// TODO Auto-generated method stub
			int sensorType = event.sensor.getType();  
			  
			  //values[0]:X轴，values[1]：Y轴，values[2]：Z轴
			  float[] values = event.values;  
			  if(sensorType == Sensor.TYPE_ACCELEROMETER){  
				   if((Math.abs(values[0])>25 || Math.abs(values[1])>25 || Math.abs(values[2])>25)){
					   //摇动手机后，设置button上显示的字为空    
					  if(mlistener != null)
					  {
						  mlistener.onShake();
					  }
				   }
			  }
		}
		
		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			
		}
	};
	
}

