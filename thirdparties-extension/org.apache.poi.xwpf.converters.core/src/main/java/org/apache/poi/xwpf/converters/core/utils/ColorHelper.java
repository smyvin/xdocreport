package org.apache.poi.xwpf.converters.core.utils;

import java.awt.Color;

import org.apache.poi.xwpf.converters.core.registry.ColorRegistry;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTShd;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STHexColor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.STShd;

public class ColorHelper
{
    private static final String AUTO = "auto";

    private static final String PCT = "pct";

    public static Color getFillColor( CTShd shd )
    {
        if ( shd == null )
        {
            return null;
        }
        STHexColor hexColor = shd.xgetFill();
        Object val = shd.xgetVal();
        return getColor( hexColor, val, true );
    }

    public static Color getColor( CTRPr rPr )
    {
        if ( rPr != null )
        {
            CTColor ctColor = rPr.getColor();
            if ( ctColor != null )
            {
                STHexColor color = ctColor.xgetVal();
                Object val = ctColor.getVal();
                return ColorHelper.getColor( color, val, false );
            }
            else
            {
                return ColorHelper.getColor( rPr.getShd() );
            }
        }
        return null;
    }

    public static Color getColor( CTShd shd )
    {
        if ( shd == null )
        {
            return null;
        }
        STHexColor hexColor = shd.xgetColor();
        Object val = shd.xgetVal();
        return getColor( hexColor, val, false );
    }

    public static Color getColor( STHexColor hexColor, Object val, boolean background )
    {
        if ( hexColor == null )
        {
            return null;
        }
        return getColor( hexColor.getStringValue(), val, background );
    }

    public static Color getColor( String hexColor, Object val, boolean background )
    {
        if ( hexColor != null )
        {
            if ( AUTO.equals( hexColor ) )
            {
                Color autoColor = background ? Color.WHITE : Color.BLACK;
                if ( val != null )
                {
                    String s = getStringVal( val );
                    if ( s.startsWith( PCT ) )
                    {
                        s = s.substring( PCT.length(), s.length() );
                        try
                        {
                            float percent = Float.parseFloat( s ) / 100;
                            if ( background )
                            {
                                return darken( autoColor.getRed(), autoColor.getGreen(), autoColor.getBlue(), percent );
                            }
                            return lighten( autoColor.getRed(), autoColor.getGreen(), autoColor.getBlue(), percent );
                        }
                        catch ( Throwable e )
                        {
                            e.printStackTrace();
                        }
                    }
                }
                return autoColor;
            }
            return ColorRegistry.getInstance().getColor( "0x" + hexColor );
        }
        return null;
    }

    private static String getStringVal( Object val )
    {
        if ( val instanceof STShd )
        {
            STShd shd = (STShd) val;
            return shd.getStringValue();
        }
        return val.toString();
    }

    public static String toHexString( Color color )
    {
        String hexaWith8Digits = Integer.toHexString( color.getRGB() );
        return new StringBuilder( "#" ).append( hexaWith8Digits.substring( 2, hexaWith8Digits.length() ) ).toString();
    }

    public static Color darken( int r, int g, int b, double percent )
        throws IllegalArgumentException
    {
        return new Color( Math.max( (int) ( r * ( 1 - percent ) ), 0 ), Math.max( (int) ( g * ( 1 - percent ) ), 0 ),
                          Math.max( (int) ( b * ( 1 - percent ) ), 0 ) );
    }

    public static Color lighten( int r, int g, int b, double percent )
        throws IllegalArgumentException
    {
        int r2, g2, b2;
        r2 = r + (int) ( ( 255 - r ) * percent );
        g2 = g + (int) ( ( 255 - g ) * percent );
        b2 = b + (int) ( ( 255 - b ) * percent );
        return new Color( r2, g2, b2 );
    }
}
