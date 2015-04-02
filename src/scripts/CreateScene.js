var JavaPackages = new JavaImporter
(
	Packages.sage.scene.Group,
	Packages.sage.scene.SceneNode,
	Packages.sage.scene.SkyBox,
	Packages.sage.scene.SkyBox.Face,
	Packages.sage.texture.Texture,
	Packages.sage.texture.TextureManager,
	Packages.java.io.File
);

with(JavaPackages)
{		
	var imagesDirectory = "." + File.separator + "bin" + File.separator + "images" + File.separator;
			
	var scene = new Group("Root Node");
	var skyBox = new SkyBox("SkyBox", 500, 500, 500);
	
	var northTexture = TextureManager.loadTexture2D(imagesDirectory + "front.png");
	var southTexture = TextureManager.loadTexture2D(imagesDirectory + "back.png");
	var eastTexture = TextureManager.loadTexture2D(imagesDirectory + "right.png");
	var westTexture = TextureManager.loadTexture2D(imagesDirectory + "left.png");
	var upTexture = TextureManager.loadTexture2D(imagesDirectory + "up.png");
	var downTexture = TextureManager.loadTexture2D(imagesDirectory + "down.png");
	
	skyBox.setTexture(SkyBox.Face.North, northTexture);
	skyBox.setTexture(SkyBox.Face.South, southTexture);
	skyBox.setTexture(SkyBox.Face.East, eastTexture);
	skyBox.setTexture(SkyBox.Face.West, westTexture);
	skyBox.setTexture(SkyBox.Face.Up, upTexture);
	skyBox.setTexture(SkyBox.Face.Down, downTexture);	
	
	scene.addChild(skyBox);
}