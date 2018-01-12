package lineClasss;


import java.awt.BorderLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
import java.lang.reflect.Array;
import java.util.Collections;
import java.util.Vector;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.*;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

public class LaneDetection
{  
	static int[][] houghArray;
	static int numpoints;
	static Mat cropped;
	
   
   public static void main( String[] args )
   {
      System.loadLibrary( Core.NATIVE_LIBRARY_NAME );
      
      //Create and set up the window.
      JFrame frame = new JFrame("OpenCV");
      
      frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

      JLabel imageHolder = new JLabel();
      JLabel basicHolder = new JLabel();
      frame.getContentPane().add(imageHolder, BorderLayout.NORTH);
      frame.getContentPane().add(basicHolder, BorderLayout.SOUTH);
      
      //Display the window.
      frame.pack();
      frame.setVisible(true);
      
    
     // String streamAddr = "http://c-cam.uchicago.edu/mjpg/video.mjpg"; //try in browser to make sure its up!
      //here is a video in the opencv installation folder!
     // String streamAddr = "C:\\Users\\Andrew\\Downloads\\opencv\\sources\\samples\\gpu\\768x576.avi";
      String streamAddr = "C:\\Users\\Andrew\\Downloads\\StayingInLane.avi";
      
      VideoCapture vcap = new VideoCapture();
      if (!vcap.open(streamAddr))
      {
    	  System.out.println("Error opening video stream");
    	  return;
      }
      
      System.out.println("Stream Opened");
      Mat img = new Mat();
      Mat basicimg = new Mat();
      Mat out = new Mat();
      Mat basic =new Mat();
      int i=0;
      while (true) 
      {
          if(!vcap.read(img)) {
              System.out.println("No frame");
          }
          else
          {
        	  try {
				Thread.sleep(0);
				          
	          //convert to greyscale
	          			Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
	          //threshold the image
	          						
	          			canny(img);
	        
	          //openCV version
	          //Imgproc.threshold(img, img, 100, 255, Imgproc.THRESH_BINARY);
	          //convert to colour so we can put text into the image using whatever colour we want!
	         
	          
	          					Imgproc.cvtColor(img, img, Imgproc.COLOR_GRAY2BGR);
	          
	         
	          BufferedImage jimg = Mat2BufferedImage(img);
	          BufferedImage Bimg = Mat2BufferedImage(img);
	     
	        
	          basicHolder.setIcon(new ImageIcon(Bimg));
	          imageHolder.setIcon(new ImageIcon(jimg));
	          
	          
	        
	          frame.pack();
	        
        	  } catch (InterruptedException e) {
  				// TODO Auto-generated catch block
  				e.printStackTrace();
  			}
          }
          
      }
   }
   

   
   
   public static BufferedImage Mat2BufferedImage(Mat m)
   {
	// source: http://answers.opencv.org/question/10344/opencv-java-load-image-to-gui/
	// Fastest code
	// The output can be assigned either to a BufferedImage or to an Image

	    int type = BufferedImage.TYPE_BYTE_GRAY;
	    if ( m.channels() > 1 ) {
	        type = BufferedImage.TYPE_3BYTE_BGR;
	    }
	    int bufferSize = m.channels()*m.cols()*m.rows();
	    byte [] b = new byte[bufferSize];
	    m.get(0,0,b); // get all the pixels
	    BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
	    final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
	    System.arraycopy(b, 0, targetPixels, 0, b.length);  
	    return image;

	}
   
//   public static void threshold(Mat img, int t)
//   {
//	   /* threshold the image (img), note here that we need to do an
//	    * & with 0xff. this is because Java uses signed two's complement
//	    * types. The & operation will give us the pixel in the range we are
//	    * used to, 0..255
//	    */
//	   byte data[] = new byte[img.rows()*img.cols()*img.channels()];
//	   img.get(0, 0, data);
//	   for (int i=0;i<data.length;i++)
//	   {
//		   int unsigned = (data[i] & 0xff);
//		   if (unsigned > t)
//			   data[i] = (byte)255;
//		   else
//			   data[i] = (byte)0;
//		   
//	   }
//	  
////	   	int lowThreshold = 60;
////		int ratio = 3;
////		Imgproc.Canny(img, img, lowThreshold, lowThreshold * ratio);
//	   
//	   
//	   
//	 
//	   img.put(0, 0, data);
//   }
   
   private static Mat canny(Mat img)
   {
	// init
			Mat grayImage = new Mat();
			Mat detectedEdges = new Mat();
			
			// convert to grayscale
			//Imgproc.cvtColor(img, grayImage, Imgproc.COLOR_BGR2GRAY);
			
			// reduce noise with a 3x3 kernel
			//Imgproc.blur(grayImage, detectedEdges, new Size(250, 250));
			
			// canny detector, with ratio of lower:upper threshold of 3:1
			//Imgproc.blur(grayImage, detectedEdges, new Size(7, 7));
			Imgproc.Canny(img, img, 150,84 * 3);
	
			Mat test = new Mat();
			
		//	Rect roi = new Rect(500, 60, 110, 110);
			
			//Imgproc.HoughLines(img, test, 1, Math.PI/250, 15);
			Rect roi = new Rect(0, 0, img.cols() - 1, img.rows() - 1);
			//Mat cropped = new Mat(img, roi);
			
			
			
			Point pt1 = new Point(600,img.cols());
			Point pt2 = new Point(img.rows(),20);
			//Core.rectangle(img, pt1, pt2, new Scalar(255,0,0, 0),5);
			//Rect roi = new Rect(0, 0, img.cols() - 1, img.rows() - 1);
			
			//Core.rectangle(img,);
			 cropped = new Mat(img, roi);
			 Imgproc.HoughLinesP(img, test, 1, Math.PI/250, 15, 26, 10);
			
			for (int x = 0; x < test.rows(); x++)
	        {
	            double[] vec = test.get(x, 0);
	            double  x1 = vec[0],
	                    y1 = vec[1],
	                    x2 = vec[2],
	                    y2 = vec[3];
	            Point start = new Point(x1, y1);
	            Point end = new Point(x2, y2);
	       	        	            
	                Core.line(img, start, end, new Scalar(255,0,0, 255),5);//(img, start, end, new Scalar(255,0,0, 0),5);
	        }
					  
			return img;
	      }
   

	
}
