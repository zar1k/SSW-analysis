package modularsystemerrorhandlingssw;

import java.io.Serializable;

/**
 *
 * @author Пелепейченко А.В. /КС-МИСАТ
 */
public class CanBit implements Serializable{
    final public long canAddr;
    final public int bit;
    public String [] name;
    final public boolean isTu;
    private String toolTip;
    
    private boolean needSetDefault = false;
    private boolean defaultValue = false;
    
    public CanBit(long canAddr, int bit, String[] name, boolean isTu){
        this.canAddr = canAddr;
        this.bit = bit;
        this.name = name;
        this.isTu = isTu;
    }
    
    @Override
    public boolean equals(Object obj) {
        try{
            CanBit cb = (CanBit) obj;
            return (canAddr==cb.canAddr) && (bit==cb.bit);
        }catch(ClassCastException ex){
            return false;
        }
    }

    @Override
    public int hashCode() {
        return ((int)(canAddr<<3)) ^ (int)bit;
    }

    
    
    public boolean isNeedSetDefault() {
        return needSetDefault;
    }

    public void setNeedSetDefault(boolean needSetDefault) {
        this.needSetDefault = needSetDefault;
    }

    public boolean getDefaultValue() {
        return defaultValue;
    }

    public void setDefaultValue(boolean defaultValue) {
        this.defaultValue = defaultValue;
    }
    
    
    public void setToolTip(String tt){toolTip = tt;}
    public String getToolTip(){return toolTip;}
    
}
