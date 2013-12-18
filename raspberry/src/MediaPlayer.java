//package klubi.raspberry.music.player;

import java.net.URI;
import java.io.File;
import javax.media.opengl.GL2ES2;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLBase;
import javax.media.opengl.GLException;
import javax.media.opengl.GLProfile;

import jogamp.opengl.GLAutoDrawableBase;

import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.JoglVersion;
import com.jogamp.opengl.util.av.GLMediaPlayer;
import com.jogamp.opengl.util.av.GLMediaPlayer.GLMediaEventListener;
import com.jogamp.opengl.util.av.GLMediaPlayer.StreamException;
import com.jogamp.opengl.util.av.GLMediaPlayerFactory;
import com.jogamp.opengl.util.texture.TextureSequence.TextureFrame;

public class MediaPlayer
{
	static GLMediaPlayer player;
	static volatile boolean stop = false;

	/**
	 * @param args
	 */
	public static void main(String[] args)
	{
		String filename = args[0];
		String resource;
		URI uri;

		System.out.println("Hello Media Player...");

                long a = com.jogamp.common.os.Platform.currentTimeMillis();
		player = GLMediaPlayerFactory.createDefault();
                long b = com.jogamp.common.os.Platform.currentTimeMillis();
                System.out.println(b-a + "ms GLMediaPlayerFactory.createDefault()");                

		System.out.println(player.toString());

		//		final GLProfile glp;
		//		glp = GLProfile.getGL2ES2();
		//		System.out.println("GLProfile: " + glp);
		//		final GLWindow window = GLWindow.create(new GLCapabilities(glp));
		//		GL2ES2 gl = window.getGL().getGL2ES2();
		//		System.out.println(JoglVersion.getGLInfo(gl, null));

		player.addEventListener(new GLMediaEventListener()
		{

			@Override
			public void newFrameAvailable(GLMediaPlayer ts, TextureFrame newFrame,
					long when) { }

			@Override
			public void attributesChanged(GLMediaPlayer mp, int event_mask, long when)
			{
				System.out.println("\n***\nEvent mask changed: " + event_mask);
				System.out.println("Timestamp: "+ when);
				System.out.println("State of player: " + player.getState().toString() +"\n");

				if ((event_mask & GLMediaEventListener.EVENT_CHANGE_INIT) !=0) {
					System.out.println("Duration: " + player.getDuration() + "ms");
					System.out.println("Volume: " + player.getAudioVolume());
					try {
						System.out.println("player.initGL()...");
						mp.initGL(null);
					}
					catch (IllegalStateException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (GLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					catch (StreamException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}else if ((event_mask & GLMediaEventListener.EVENT_CHANGE_PAUSE) !=0) {
					System.out.println("player.play()...");
					System.out.println("mp.setPlaySpeed(1f) returned: " + mp.setPlaySpeed(1f));
					mp.seek(0);
					mp.play();
				}else if ((event_mask & GLMediaEventListener.EVENT_CHANGE_PLAY) !=0) {
					System.out.println("playing...");
					System.out.println(mp.toString());
					System.out.println(mp.getAudioSink().toString());
				}
				if( 0 != ( ( GLMediaEventListener.EVENT_CHANGE_ERR | GLMediaEventListener.EVENT_CHANGE_EOS ) & event_mask ) ) {
					final StreamException se = player.getStreamException();
					if( null != se ) {
						se.printStackTrace();
					}
					new Thread() {
						public void run() {
							System.out.println("terminating...");							
							player.destroy(null);
							stop = true;
						}
					}.start();
				}

			}
		});

		if (player==null) {
			System.out.println("Failed to create player!");
		} else {
			System.out.println("Created new player: " + player.getClass().getName());
			try {
				System.out.println("filename = " +filename);
				if(filename.equals("")){
					System.out.println("No file selected");
					System.exit(0);
				}
			        File file = new File(filename);
                                if(!file.exists()){	
					System.out.println("File do not exist");
                                        System.exit(0);
				}
				uri = file.toURI();
				System.out.println("State of player: " + player.getState().toString());
				System.out.println("...initializing stream...");

				player.initStream(uri, GLMediaPlayer.STREAM_ID_NONE, GLMediaPlayer.STREAM_ID_AUTO, GLMediaPlayer.TEXTURE_COUNT_DEFAULT);
			}
			catch (Exception e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}

			// Main thread waits until playback is done
			StreamException se = null;
			while( null == se && stop == false ) {
				try {
					Thread.sleep(100);
				} catch (InterruptedException e) { }
				se = player.getStreamException();
			}
			if( null != se ) {
				se.printStackTrace();
				throw new RuntimeException(se);
			}


		}
		System.out.println("...main exit...");

	}


}
