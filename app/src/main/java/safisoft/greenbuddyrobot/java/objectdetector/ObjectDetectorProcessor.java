/*
 * Copyright 2020 Google LLC. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package safisoft.greenbuddyrobot.java.objectdetector;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.objects.DetectedObject;
import com.google.mlkit.vision.objects.ObjectDetection;
import com.google.mlkit.vision.objects.ObjectDetector;
import com.google.mlkit.vision.objects.ObjectDetectorOptionsBase;

import java.util.List;

import safisoft.greenbuddyrobot.GraphicOverlay;
import safisoft.greenbuddyrobot.java.VisionProcessorBase;

/** A processor to run object detector. */
public class ObjectDetectorProcessor extends VisionProcessorBase<List<DetectedObject>> {


  public static int Main_centerY;
  public static int Main_centerX;


  public static String Item_Name ;
  public static int Top ;
    public static int Left;
    public static int Right ;
    public static int Bottom ;

    public static int Width ;
    public static int Height ;
    public static int CenterY ;
    public static int CenterX ;

    public static Rect t ;


  private static final String TAG = "ObjectDetectorProcessor";

  private final ObjectDetector detector;

  public ObjectDetectorProcessor(Context context, ObjectDetectorOptionsBase options) {
    super(context);
    detector = ObjectDetection.getClient(options);
  }

  @Override
  public void stop() {
    super.stop();
    detector.close();
  }

  @Override
  protected Task<List<DetectedObject>> detectInImage(InputImage image) {
    return detector.process(image);
  }

  @Override
  protected void onSuccess(
      @NonNull List<DetectedObject> results, @NonNull GraphicOverlay graphicOverlay) {
    for (DetectedObject object : results) {
      graphicOverlay.add(new ObjectGraphic(graphicOverlay, object));

      t = object.getBoundingBox(); //position
      Main_centerX = object.getBoundingBox().centerX();//size
      Main_centerY = object.getBoundingBox().centerY();


      if(object.getLabels().size() > 0){
          Item_Name = object.getLabels().get(0).getText();
      }
      Top = t.top;
      Left =t.left;
      Right = t.right;
      Bottom = t.bottom;
      Width = t.width();
      Height = t.height();
      CenterX = t.centerX();
      CenterY = t.centerY();

    }
  }

  @Override
  protected void onFailure(@NonNull Exception e) {
    Log.e(TAG, "Object detection failed!", e);
  }

    public String Get_Name(){return Item_Name;}
    public int Get_Top() {return Top;}
    public int Get_Left() {return Left;}
    public int Get_Right() {return Right;}
    public int Get_Bottom() {return Bottom;}
    public int Get_Width() {return Width;}
    public int Get_Height() {return Height;}
    public int Get_CenterX() {return CenterX;}
    public int Get_CenterY() {return CenterY;}


}

