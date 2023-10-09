
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;



public class EdgeDetection {
    private static int[][] KERNEL=new int[][]{{0,-1,0},{-1,4,-1},{0,-1,0}};

    public static void EdgeDetectionTask(String resource,int degree) throws IOException {
        BufferedImage bufferedImage= ImageIO.read(new File(resource));
        BufferedImage resultImage=new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_RGB);
        BufferedImage[] subImages=splitImage(bufferedImage,degree,1);
        for(int i=0;i<subImages.length;i++){
            final int index=i;
            new Thread(()->{
                try {
                    processImage(subImages[index],index,resultImage);

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
    private static BufferedImage[] splitImage(BufferedImage image,int rows,int cols) throws IOException {
        int chunks = rows * cols;

        int chunkWidth = image.getWidth() / cols; // determines the chunk width and height
        int chunkHeight = image.getHeight() / rows;
        int count = 0;
        BufferedImage imgs[] = new BufferedImage[chunks]; //Image array to hold image chunks
        for (int x = 0; x < rows; x++) {
            for (int y = 0; y < cols; y++) {
                //Initialize the image array with image chunks

                int imageType = image.getType();
                if (imageType == 0) {
                    imageType = 5;
                }

                imgs[count] = new BufferedImage(chunkWidth, chunkHeight, imageType);

                // draws the image chunk
                Graphics2D gr = imgs[count++].createGraphics();
                gr.drawImage(image, 0, 0, chunkWidth, chunkHeight, chunkWidth * y, chunkHeight * x,
                        chunkWidth * y + chunkWidth, chunkHeight * x + chunkHeight, null);
                gr.dispose();
            }
        }
        return imgs;


    }
    private static void processImage(BufferedImage bufferedImage,int id,BufferedImage destinationImage) throws IOException {
        BufferedImage newImage=new BufferedImage(bufferedImage.getWidth(),bufferedImage.getHeight(),BufferedImage.TYPE_INT_RGB);
        int[][] pixels=new int[3][3];
        Color[][] pixColor=new Color[3][3];
        int A,R,G,B;

        for (int y = 0; y < bufferedImage.getHeight()-2 ; y++) {

            for (int x = 0; x < bufferedImage.getWidth()-2; x++) {

                pixels[0][0] = bufferedImage.getRGB(x, y);
                pixColor[0][0] = new Color(pixels[0][0], true);
                pixels[0][1] = bufferedImage.getRGB(x, y + 1);
                pixColor[0][1] = new Color(pixels[0][1], true);
                pixels[0][2] = bufferedImage.getRGB(x, y + 2);
                pixColor[0][2] = new Color(pixels[0][2], true);
                pixels[1][0] = bufferedImage.getRGB(x + 1, y);
                pixColor[1][0] = new Color(pixels[1][0], true);
                pixels[1][1] = bufferedImage.getRGB(x + 1, y + 1);
                pixColor[1][1] = new Color(pixels[1][1], true);
                pixels[1][2] = bufferedImage.getRGB(x + 1, y + 2);
                pixColor[1][2] = new Color(pixels[1][2], true);
                pixels[2][0] = bufferedImage.getRGB(x + 2, y);
                pixColor[2][0] = new Color(pixels[2][0], true);
                pixels[2][1] = bufferedImage.getRGB(x + 2, y + 1);
                pixColor[2][1] = new Color(pixels[2][1], true);
                pixels[2][2] = bufferedImage.getRGB(x + 2, y + 2);
                pixColor[2][2] = new Color(pixels[2][2], true);
                A = pixColor[1][1].getAlpha();

                R = (int) ((((pixColor[0][0].getRed() * KERNEL[0][0]) +
                        (pixColor[1][0].getRed() * KERNEL[1][0]) +
                        (pixColor[2][0].getRed() * KERNEL[2][0]) +
                        (pixColor[0][1].getRed() * KERNEL[0][1]) +
                        (pixColor[1][1].getRed() * KERNEL[1][1]) +
                        (pixColor[2][1].getRed() * KERNEL[2][1]) +
                        (pixColor[0][2].getRed() * KERNEL[0][2]) +
                        (pixColor[1][2].getRed() * KERNEL[1][2]) +
                        (pixColor[2][2].getRed() * KERNEL[2][2]))));
                if (R < 0)
                    R = 0;
                else if (R > 255)
                    R = 255;

                G = (int) ((((pixColor[0][0].getGreen() * KERNEL[0][0]) +
                        (pixColor[1][0].getGreen() * KERNEL[1][0]) +
                        (pixColor[2][0].getGreen() * KERNEL[2][0]) +
                        (pixColor[0][1].getGreen() * KERNEL[0][1]) +
                        (pixColor[1][1].getGreen() * KERNEL[1][1]) +
                        (pixColor[2][1].getGreen() * KERNEL[2][1]) +
                        (pixColor[0][2].getGreen() * KERNEL[0][2]) +
                        (pixColor[1][2].getGreen() * KERNEL[1][2]) +
                        (pixColor[2][2].getGreen() * KERNEL[2][2]))));
                if (G < 0) {
                    G = 0;
                } else if (G > 255) {
                    G = 255;
                }

                B = (int) ((((pixColor[0][0].getBlue() * KERNEL[0][0]) +
                        (pixColor[1][0].getBlue() * KERNEL[1][0]) +
                        (pixColor[2][0].getBlue() * KERNEL[2][0]) +
                        (pixColor[0][1].getBlue() * KERNEL[0][1]) +
                        (pixColor[1][1].getBlue() * KERNEL[1][1]) +
                        (pixColor[2][1].getBlue() * KERNEL[2][1]) +
                        (pixColor[0][2].getBlue() * KERNEL[0][2]) +
                        (pixColor[1][2].getBlue() * KERNEL[1][2]) +
                        (pixColor[2][2].getBlue() * KERNEL[2][2]))));
                if (B < 0)
                    B = 0;
                else if (B > 255)
                    B = 255;
                Color c = new Color(R, G, B, A);
                newImage.setRGB(x+1,y+1,c.getRGB());

            }
        }
        Graphics2D g2=destinationImage.createGraphics();
        g2.drawImage(newImage,0,id*newImage.getHeight(),null);
        g2.dispose();

        ImageIO.write(destinationImage,"JPG",new File("resultImage.jpg"));
    }
    public static void main(String[] args) {
        try{
            EdgeDetection.EdgeDetectionTask(args[0],8);
        }catch(IOException ex){
            ex.printStackTrace();
        }

    }

}