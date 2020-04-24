import java.awt.*;
import java.io.Serializable;

public class Player implements Serializable
{
    private static final long serialVersionUID = 1L;

    private  String  name;
    private Color playerColor;
    private Color opponentColor;


    public Player(String name)
    {
        super();
        this.name   = name;
        if(name.equals("RED")) {
            this.playerColor = new Color(255,0,0);
            this.opponentColor = new Color(0,0,255);
        } else if (name.equals("BLUE")) {
            this.playerColor = new Color(0,0,255);
            this.playerColor = new Color(255,0,0);
        }
    }
    @Override
    public boolean equals(Object player)
    {
        Player ply = (Player)player;
        return this.getName().equals(ply.getName());
    }

    public String getName() {
        return this.name;
    }

}