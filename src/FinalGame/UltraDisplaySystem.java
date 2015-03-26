package FinalGame;

import java.awt.Canvas;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.KeyListener;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JFrame;

import sage.display.DisplaySystem;
import sage.display.IDisplaySystem;
import sage.renderer.IRenderer;
import sage.renderer.RendererFactory;

public class UltraDisplaySystem implements IDisplaySystem
{
	private JFrame myFrame;
	private GraphicsDevice device;
	private IRenderer myRenderer;
	private int width, height, bitDepth, refreshRate;
	private Canvas rendererCanvas;
	private boolean isCreated;
	private boolean isFullScreen;

	public UltraDisplaySystem(int width, int height, int bitDepth, int refreshRate, boolean isFS, String rendererClassName)
	{
		this.width = width;
		this.height = height;
		this.bitDepth = bitDepth;
		this.refreshRate = refreshRate;
		this.isFullScreen = isFS;
		// get a renderer from the RendererFactory
		myRenderer = RendererFactory.createRenderer(rendererClassName);
		if (myRenderer == null)
		{
			throw new RuntimeException("Unable to find renderer '" + rendererClassName + "'");
		}

		rendererCanvas = myRenderer.getCanvas();
		myFrame = new JFrame("Default Title");
		myFrame.add(rendererCanvas);
		// initialize the screen with the specified parameters
		DisplayMode displayMode = new DisplayMode(this.width, this.height, this.bitDepth, this.refreshRate);
		initScreen(displayMode, isFullScreen);
		// save DisplaySystem, show the frame and indicate DisplaySystem is created
		DisplaySystem.setCurrentDisplaySystem(this);
		myFrame.setVisible(true);
		isCreated = true;
	}

	private void initScreen(DisplayMode dispMode, boolean fullScreenRequested)
	{
		GraphicsEnvironment environment = GraphicsEnvironment.getLocalGraphicsEnvironment();
		device = environment.getDefaultScreenDevice();
		if (device.isFullScreenSupported() && fullScreenRequested)
		{
			// suppress title bar, borders, etc. 
			myFrame.setUndecorated(true);
			// full-screen so not resizeable
			myFrame.setResizable(false);
			// ignore AWT repaints
			myFrame.setIgnoreRepaint(true);
			
			// Put device in full-screen mode. Note that this must be done BEFORE attempting
			// to change the DisplayMode; the application must first own the screen (i.e., has FSEM)
			device.setFullScreenWindow(myFrame);
			// try to set the full-screen device DisplayMode
			if (dispMode != null && device.isDisplayChangeSupported())
			{
				try
				{
//					device.
					device.setDisplayMode(dispMode);
					myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
				}
				catch (Exception ex)
				{
					System.err.println("Exception while setting device DisplayMode: " + ex);
				}
			}
			else
			{
				System.err.println("Cannot set display mode");
			}
		}
		else
		{
			// use windowed mode – set JFrame characteristics
			myFrame.setSize(dispMode.getWidth(), dispMode.getHeight());
			// centers window on screen
			myFrame.setLocationRelativeTo(null);  
		}
	}

	@Override
	public void close()
	{
		if (device != null)
		{ 
			Window window = device.getFullScreenWindow(); 
		
			if (window != null)
			{
				window.dispose();
			}
		
			device.setFullScreenWindow(null); 
		}
	}

	@Override
	public void addKeyListener(KeyListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseListener(MouseListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void addMouseMotionListener(MouseMotionListener arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void convertPointToScreen(Point arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getBitDepth()
	{
		return bitDepth;
	}

	@Override
	public int getHeight()
	{
		return height;
	}

	@Override
	public int getRefreshRate()
	{
		return refreshRate;
	}

	@Override
	public IRenderer getRenderer()
	{
		return myRenderer;
	}

	@Override
	public int getWidth()
	{
		return width;
	}

	@Override
	public boolean isCreated()
	{
		return isCreated;
	}

	@Override
	public boolean isFullScreen()
	{
		return isFullScreen;
	}

	@Override
	public boolean isShowing()
	{
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void setBitDepth(int arg0)
	{
		bitDepth = arg0;
	}

	@Override
	public void setCustomCursor(String arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setHeight(int arg0)
	{
		height = arg0;
	}

	@Override
	public void setPredefinedCursor(int arg0)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setRefreshRate(int arg0)
	{
		refreshRate = arg0;
	}

	@Override
	public void setTitle(String arg0)
	{
		myFrame.setTitle(arg0);
	}

	@Override
	public void setWidth(int arg0)
	{
		width = arg0;
	}
}
