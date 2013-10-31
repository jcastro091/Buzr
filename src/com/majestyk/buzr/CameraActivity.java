package com.majestyk.buzr;
//http://commonsware.com/blog/2013/01/23/no-android-does-not-have-crop-intent.html

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.hardware.Camera.Parameters;
import android.hardware.Camera.PictureCallback;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.aviary.android.feather.FeatherActivity;

import eu.janmuller.android.simplecropimage.CropImage;

public class CameraActivity extends Activity implements SensorEventListener, OnClickListener {

	private CameraActivity thiz;

	private String PHOTO_FILE;
	private final String PHOTO_FILE_DIR = "/Buzr";
	private final String PHOTO_FILE_TYPE = ".jpg";
	private final String PHOTO_FILE_NAME = "IMG_";
	//	private final String TEMP_PHOTO_FILE = "TEMP_PHOTO.jpg";
	private final String TAG = getClass().getSimpleName();

	private File imageFile;
	private File imageFileCrop;

	private Camera mCamera;
	private CameraPreview mPreview;
	private int mCameraId;
	private SensorManager sensorManager = null;
	private ExifInterface exif;
	private int orientation, 
	deviceHeight,
	deviceWidth;
	private Button ibCapture,
	ibFlash,
	ibFlip,
	ibGrid;
	private ImageView grid,
	ibGallery;

	private int degrees = -1;

	@SuppressWarnings("deprecation")
	@Override
	public final void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.camera);
		thiz = this;

		// Getting all the needed elements from the layout
		ibCapture = (Button) findViewById(R.id.ibCapture);
		ibFlash = (Button) findViewById(R.id.ibFlash);
		ibFlip = (Button) findViewById(R.id.ibFlip);
		ibGrid = (Button) findViewById(R.id.ibGrid);
		ibGallery = (ImageView) findViewById(R.id.ibGallery);

		// Get top image from Camera Roll
		String[] projection = {
				MediaStore.Images.Media._ID,
				MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
				MediaStore.Images.Media.DISPLAY_NAME
		};
		String selection = MediaStore.Images.Media.BUCKET_DISPLAY_NAME + " = ?";
		String[] selectionArgs = new String[] { "Camera" };
		Cursor mImageCursor = managedQuery(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
				projection, selection, selectionArgs, null);
		mImageCursor.moveToLast();
		int idx1 = mImageCursor.getColumnIndex(MediaStore.Images.Media._ID);
		int idx2 = mImageCursor.getColumnIndex(MediaStore.Images.Media.DISPLAY_NAME);
		Log.i(TAG, "Thumbnail ID: " + idx1);
		if (idx1 >= 0) {
			String imgId = mImageCursor.getString(idx1);
			Log.d("MainActivity","Count - " +  + mImageCursor.getPosition() +
					" Id - " + imgId + " -> " + mImageCursor.getString(idx2));
			Bitmap bm = MediaStore.Images.Thumbnails.getThumbnail(getContentResolver(),
					mImageCursor.getLong(idx1), MediaStore.Images.Thumbnails.MINI_KIND, null);
			ibGallery.setImageBitmap(bm);
		}
		grid = (ImageView) findViewById(R.id.grid);

		// Getting the sensor service.
		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

		// Selecting the resolution of the Android device so we can create a proportional preview
		Display display = ((WindowManager) getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		deviceHeight = display.getHeight();
		deviceWidth = display.getWidth();

		// Add a listener to the Capture button
		ibCapture.setOnClickListener(this);

		ibFlash.setOnClickListener(this);
		ibFlip.setOnClickListener(this);
		ibGrid.setOnClickListener(this);
		ibGallery.setOnClickListener(this);
	}

	public final void onClick(View v) {
		switch (v.getId()) {
		case R.id.ibCapture:
			mCamera.takePicture(null, null, mPicture);
			break;
		case R.id.ibFlash:
			if (getPackageManager().hasSystemFeature(
					PackageManager.FEATURE_CAMERA_FLASH)) {
				Parameters p = mCamera.getParameters();

				if (p.getFlashMode().equals(Parameters.FLASH_MODE_ON)) {
					p.setFlashMode(Parameters.FLASH_MODE_OFF);
					ibFlash.setSelected(false);
				} else if (p.getFlashMode().equals(Parameters.FLASH_MODE_OFF)) {
					p.setFlashMode(Parameters.FLASH_MODE_ON);
					ibFlash.setSelected(true);
				}

				mCamera.setParameters(p);
			}
			break;
		case R.id.ibFlip:
			if (Camera.getNumberOfCameras() > 1) {
				releaseCamera();

				FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
				preview.removeViewAt(0);

				if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
					mCameraId = Camera.CameraInfo.CAMERA_FACING_FRONT;
				else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
					mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;

				createCamera(mCameraId);

				sensorManager.registerListener(thiz, sensorManager.getDefaultSensor(
						Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
			} else {
				// Toast.makeText(thiz, "", Toast.LENGTH_SHORT).show();
			}
			break;
		case R.id.ibGrid:
			if(grid.getVisibility() == View.VISIBLE)
				grid.setVisibility(View.GONE);
			else if(grid.getVisibility() == View.GONE)
				grid.setVisibility(View.VISIBLE);
			break;
		case R.id.ibGallery:
			Intent i = new Intent(Intent.ACTION_PICK, 
					android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI); 
			startActivityForResult(i, GlobalValues.REQUEST_CODE_IMAGE_MEDIA_TYPE);
			break;
		}
	}

	@SuppressWarnings("deprecation")
	private final void createCamera(int cameraId) {
		// Create an instance of Camera
		mCamera = getCameraInstance(cameraId);

		// Setting the right parameters in the camera
		Camera.Parameters params = mCamera.getParameters();
		params.setPictureFormat(PixelFormat.JPEG);
		params.setJpegQuality(85);

		// Set Display Orientation
		Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();
		int displayRotation = display.getRotation();
		switch (displayRotation)  {
		case Surface.ROTATION_0:
			break;
		case Surface.ROTATION_90:
			break;
		case Surface.ROTATION_180:
			break;
		case Surface.ROTATION_270:
			break;
		}

		int width = display.getWidth();
		params.setPictureSize(width, width);

		mCamera.setDisplayOrientation(90);
		mCamera.setParameters(params);

		// Create our Preview view and set it as the content of our activity.
		mPreview = new CameraPreview(this, mCamera);
		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);

		RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(deviceWidth, deviceHeight);
		preview.setLayoutParams(layoutParams);

		// Adding the camera preview after the FrameLayout and before the button as a separated element.
		preview.addView(mPreview, 0);
	}

	@Override
	public final void onResume() {
		super.onResume();

		if (!checkCameraHardware(this)) {
			Log.e(TAG, "Camera could not be found");
		} else if (!checkSDCard()) {
			Log.e(TAG, "SDCard could not be found");
		}

		// Creating the camera
		mCameraId = Camera.CameraInfo.CAMERA_FACING_BACK;
		createCamera(mCameraId);

		// Register this class as a listener for the accelerometer sensor
		sensorManager.registerListener(this, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), SensorManager.SENSOR_DELAY_NORMAL);
	}

	@Override
	public final void onPause() {
		super.onPause();
		releaseCamera();

		FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
		preview.removeViewAt(0);
	}

	private final void releaseCamera() {
		if (mCamera != null) {
			mCamera.release();
			mCamera = null;
		}
	}

	/** Check if this device has a camera */
	private final boolean checkCameraHardware(Context context) {
		if (context.getPackageManager().hasSystemFeature(
				PackageManager.FEATURE_CAMERA)) {
			return true;
		} else {
			return false;
		}
	}

	private final boolean checkSDCard() {
		boolean state = false;
		String status = Environment.getExternalStorageState();        
		if (status.equals(Environment.MEDIA_MOUNTED))
			state = true;
		return state;
	}

	/**
	 * A safe way to get an instance of the Camera object.
	 */
	private final Camera getCameraInstance(int cameraId) {
		Camera c = null;
		try {
			c = Camera.open(cameraId);
		} catch (Exception e) {
			Log.e(TAG, "Camera could not be opened");
		}
		return c;
	}

	private final PictureCallback mPicture = new PictureCallback() {

		public final void onPictureTaken(byte[] data, Camera camera) {

			// File of the image that we took.
			imageFile = getImageFile();
			Log.i(TAG, imageFile.toString());

			imageFile = ByteArrayToFile(data, imageFile);

			if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
				if(orientation == ExifInterface.ORIENTATION_ROTATE_90 ||
				orientation == ExifInterface.ORIENTATION_ROTATE_270)
					flipImage(data, imageFile);

			// Adding Exif data for the orientation.
			try {
				// Setting all the path for the image
				exif = new ExifInterface(imageFile.getAbsolutePath());
				exif.setAttribute(ExifInterface.TAG_ORIENTATION, "" + orientation);
				exif.saveAttributes();
			} catch (IOException e) {
				e.printStackTrace();
			}

			performCrop(getImageUri(imageFile));

		}

	};

	/**
	 * Putting in place a listener so we can get the sensor data only when
	 * something changes.
	 */
	public final void onSensorChanged(SensorEvent event) {
		synchronized (this) {
			if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				if (event.values[0] < 10 && event.values[0] > -10) {
					if (event.values[1] > 0 && orientation != ExifInterface.ORIENTATION_ROTATE_90) {
						// UP
						if (Camera.getNumberOfCameras() < 2) {
							if (android.os.Build.VERSION.SDK_INT < 4.3) {
								if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
									orientation = ExifInterface.ORIENTATION_ROTATE_270;
								else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
									orientation = ExifInterface.ORIENTATION_ROTATE_90;
							} else {
								if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
									orientation = ExifInterface.ORIENTATION_NORMAL;
								else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
									orientation = ExifInterface.ORIENTATION_NORMAL;
							}
						} else if (Camera.getNumberOfCameras() > 1) {
							if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
								Log.i(TAG, "Back camera - up-side-up");
								orientation = ExifInterface.ORIENTATION_ROTATE_90;
							} else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
								orientation = ExifInterface.ORIENTATION_ROTATE_270;
								Log.i(TAG, "Front camera - up-side-up");
							}
						}
						degrees = 0;
					} else if (event.values[1] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_270) {
						// UP SIDE DOWN
						if (Camera.getNumberOfCameras() < 2) {
							if (android.os.Build.VERSION.SDK_INT < 4.3) {
								if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
									orientation = ExifInterface.ORIENTATION_ROTATE_90;
								else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
									orientation = ExifInterface.ORIENTATION_ROTATE_270;
							} else {
								if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK)
									orientation = ExifInterface.ORIENTATION_NORMAL;
								else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT)
									orientation = ExifInterface.ORIENTATION_NORMAL;
							}
						} else if (Camera.getNumberOfCameras() > 1) {
							if (mCameraId == Camera.CameraInfo.CAMERA_FACING_BACK) {
								orientation = ExifInterface.ORIENTATION_ROTATE_270;
								Log.i(TAG, "Back camera - up-side-down");
							} else if (mCameraId == Camera.CameraInfo.CAMERA_FACING_FRONT) {
								orientation = ExifInterface.ORIENTATION_ROTATE_90;
								Log.i(TAG, "Front camera - up-side-down");
							}
						}
						degrees = 180;
					}
				} else if (event.values[1] < 10 && event.values[1] > -10) {
					if (event.values[0] > 0 && orientation != ExifInterface.ORIENTATION_NORMAL) {
						// LEFT
						orientation = ExifInterface.ORIENTATION_NORMAL;
						degrees = 90;
					} else if (event.values[0] < 0 && orientation != ExifInterface.ORIENTATION_ROTATE_180) {
						// RIGHT
						orientation = ExifInterface.ORIENTATION_ROTATE_180;
						degrees = 270;
					}

				}
				ibCapture.startAnimation(getRotateAnimation(degrees));
				ibFlash.startAnimation(getRotateAnimation(degrees));
				ibFlip.startAnimation(getRotateAnimation(degrees));
				ibGrid.startAnimation(getRotateAnimation(degrees));
			}
		}
	}

	/**
	 * Calculating the degrees needed to rotate the image imposed on the button
	 * so it is always facing the user in the right direction
	 * 
	 * @param toDegrees
	 * @return
	 */
	private final RotateAnimation getRotateAnimation(float toDegrees) {
		float compensation = 0;

		if (Math.abs(degrees - toDegrees) > 180) {
			compensation = 360;
		}

		if (toDegrees == 0) {
			compensation = -compensation;
		}

		RotateAnimation animation = new RotateAnimation(degrees, toDegrees - compensation, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
		animation.setDuration(250);
		animation.setFillAfter(true);

		return animation;
	}

	public final void onAccuracyChanged(Sensor sensor, int accuracy) { }

	@Override
	public final void onActivityResult( int requestCode, int resultCode, Intent data ) {
		super.onActivityResult(requestCode, resultCode, data);

		Intent res = new Intent();

		if (resultCode == RESULT_OK) {
			switch( requestCode ) {
			case GlobalValues.REQUEST_CODE_IMAGE_CROP:

				if (getIntent().getBooleanExtra("mode", true)) {
					Intent intent = new Intent(CameraActivity.this, FeatherActivity.class);
					intent.setData(getImageUri(imageFileCrop));
					startActivityForResult(intent, GlobalValues.ACTION_REQUEST_FEATHER);
				} else {
					res.putExtra("URI", getImageUri(imageFileCrop));
					setResult(GlobalValues.ACTION_REQUEST_FEATHER, res);
					finish();
				}

				break;
			case GlobalValues.ACTION_REQUEST_FEATHER:
				Uri imageUri = data.getData();

				res.putExtra("URI", imageUri);
				setResult(GlobalValues.ACTION_REQUEST_FEATHER, res);
				finish();
				break;
			case GlobalValues.REQUEST_CODE_IMAGE_MEDIA_TYPE:
				Uri selectedImage = Uri.parse(GlobalValues.getRealPathFromURI(
						CameraActivity.this, data.getData()));
				performCrop(selectedImage);
				break;
			}
		}
	}

	private final void performCrop(Uri fileUri) {
		try {
			imageFileCrop = getImageFile(); 
			Intent cropIntent = new Intent(this, CropImage.class);
			cropIntent.putExtra("aspectX", 1);
			cropIntent.putExtra("aspectY", 1);
			cropIntent.putExtra("outputX", 720);
			cropIntent.putExtra("outputY", 720);
			cropIntent.putExtra("scale", true);
			cropIntent.putExtra("return-data", false);
			cropIntent.putExtra("image-path-i", GlobalValues.getRealPathFromURI(this, fileUri));
			cropIntent.putExtra("image-path-o", GlobalValues.getRealPathFromURI(this, Uri.fromFile(imageFileCrop)));
			startActivityForResult(cropIntent, GlobalValues.REQUEST_CODE_IMAGE_CROP);
		}
		catch(ActivityNotFoundException anfe) {
			anfe.printStackTrace();
			String errorMessage = "Sorry, but your device does not support image size";
			Toast toast = Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	//	private final Uri getTempUri() {
	//		return Uri.fromFile(getTempFile());
	//	}

	//	private final File getTempFile() {
	//		if (checkSDCard()) {
	//			File f = new File(Environment.getExternalStorageDirectory(), TEMP_PHOTO_FILE);
	//			try {
	//				f.createNewFile();
	//			} catch (IOException e) {
	//				e.printStackTrace();
	//			}
	//			return f;
	//		} else {
	//			return null;
	//		}
	//	}

	//	private final void copy(File src, File dst) throws IOException {
	//		InputStream in = new FileInputStream(src);
	//		OutputStream out = new FileOutputStream(dst);
	//
	//		byte[] buf = new byte[1024];
	//		int len;
	//		while ((len = in.read(buf)) > 0) {
	//			out.write(buf, 0, len);
	//		}
	//
	//		in.close();
	//		out.close();
	//	}

	private final Uri getImageUri(File imageFile) {
		return Uri.fromFile(imageFile);
	}

	private final File getImageFile() {
		File dir = new File(Environment.getExternalStoragePublicDirectory(
				Environment.DIRECTORY_PICTURES), PHOTO_FILE_DIR);
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e(TAG, "Failed to create storage directory.");
				return null;
			}
		}
		String timeStamp = 
				new SimpleDateFormat("yyyMMdd_HHmmss", Locale.US).format(new Date());

		PHOTO_FILE = PHOTO_FILE_NAME + timeStamp + PHOTO_FILE_TYPE;
		return new File(dir.getPath() + File.separator + PHOTO_FILE);
	}

	private final void flipImage(byte[] data, File file) {
		int width, height;
		Matrix matrix = new Matrix();
		Camera.CameraInfo info = new Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(mCameraId, info);

		// Convert ByteArray to Bitmap
		Bitmap bitPic = BitmapFactory.decodeByteArray(data, 0, data.length);
		width = bitPic.getWidth();
		height = bitPic.getHeight();

		// Perform matrix rotations/mirrors depending on camera that took the photo
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT)
		{
			float[] mirrorY = {-1, 0, 0, 0, 1, 0, 0, 0, 1};
			Matrix matrixMirrorY = new Matrix();
			matrixMirrorY.setValues(mirrorY);

			matrix.postConcat(matrixMirrorY);
		}

		matrix.postRotate(90);

		// Create new Bitmap out of the old one
		Bitmap bitPicFinal = Bitmap.createBitmap(bitPic, 0, 0, width, height, matrix, true);

		FileOutputStream outStream;
		try {
			outStream = new FileOutputStream(file);
			bitPicFinal.compress(Bitmap.CompressFormat.PNG, 85, outStream);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		bitPic.recycle();
	}

	//	private final byte[] FileToByteArray(byte[] data, File file) {
	//		try {
	//			FileInputStream stream = new FileInputStream(file);
	//			stream.read(data);
	//			stream.close();
	//		} catch (FileNotFoundException e) {
	//			Log.d("DG_DEBUG", "File not found: " + e.getMessage());
	//		} catch (IOException e) {
	//			Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
	//		}
	//		return data;
	//	}

	private final File ByteArrayToFile(byte[] data, File file) {
		try {
			FileOutputStream stream = new FileOutputStream(file);
			stream.write(data);
			stream.close();
		} catch (FileNotFoundException e) {
			Log.d("DG_DEBUG", "File not found: " + e.getMessage());
		} catch (IOException e) {
			Log.d("DG_DEBUG", "Error accessing file: " + e.getMessage());
		}
		return file;
	}

}

/*
 * Resources:
 * * Camera *
 * 
 * http://stackoverflow.com/questions/10283467/android-front-facing-camera-taking-inverted-photos
 * 
 * * Camera Roll *
 * http://viralpatel.net/blogs/pick-image-from-galary-android-app/
 * https://gist.github.com/serggl/3131537
 * 
 * * Crop *
 * http://www.androidworks.com/crop_large_photos_with_android
 * 
 */