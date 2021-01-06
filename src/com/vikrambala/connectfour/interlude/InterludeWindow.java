package com.vikrambala.connectfour.interlude;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.vikrambala.connectfour.error.*;

/**
 * This class is used to display an interlude screen after the use successfully logs in. 
 * It will display a random meme out of a collection of four, as well as a message
 * saying that the game is loading. 
 * @author vikrambala
 *
 */
@SuppressWarnings("serial")
public class InterludeWindow extends JPanel {
    
    private BufferedImage interludeImage;
    
    List<String> memeList = new LinkedList<String>(); 
    
    public InterludeWindow() {
        
        memeList.add("memes/wholesome.jpeg");
        memeList.add("memes/j7l2ebaabqo21.jpg");
        memeList.add("memes/g0yX34o.png");
        memeList.add("memes/cbq06qopykd01.png");
        
        try {
            
            Random rand = new Random(); 
            int randomInt = rand.nextInt(4); 
            String memeImagePath = memeList.get(randomInt); 
            interludeImage = ImageIO.read(new File(memeImagePath)); 
        } catch (IOException ex) {
            ErrorHandler.errorHandler(ErrorCode.IMAGE_RETRIVAL_ERROR);
        }
        
       
    }
    
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        g.drawImage(interludeImage, 0, 0, this);
    }
    
    /**
     * Returns the size of the game board.
     */
    @Override
    public Dimension getPreferredSize() {
        return new Dimension(interludeImage.getWidth(), interludeImage.getHeight());
    }

}
