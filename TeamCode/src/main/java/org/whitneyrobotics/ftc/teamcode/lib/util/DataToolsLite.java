package org.whitneyrobotics.ftc.teamcode.lib.util;

import com.qualcomm.robotcore.util.ReadWriteFile;

import org.firstinspires.ftc.robotcore.internal.system.AppUtil;

public class DataToolsLite {

    public enum ObjectTypes{
        STRING(0),
        INT(1),
        DOUBLE(2),
        BOOLEAN(3);
        private final int value;
        ObjectTypes(int value){this.value = value;}
    }

    public static void encode(String fileName, Object... unlabeledData){
        String content = "";
        for(int i = 0; i< unlabeledData.length; i++){
            content += String.format("%s=%s,",i,unlabeledData[i]);
        }
        ReadWriteFile.writeFile(AppUtil.getInstance().getSettingsFile(fileName),content);
    }

    public static void encode(String fileName, DataTools.Data data){
        String content = "";
        for(Object i : data.keySet()){
            content += (String.format("%s=%s,",i.toString(), data.get(i).toString()));
        }
        ReadWriteFile.writeFile(AppUtil.getInstance().getSettingsFile(fileName),content);
    }

    public static String[] decode(String fileName){
        String content = ReadWriteFile.readFile(AppUtil.getInstance().getSettingsFile(fileName));
        DataTools.Data output = new DataTools.Data();
        String[] contentDivided = content.split(",");
        String[] values = new String[contentDivided.length];
        for(int i = 0; i< contentDivided.length; i++){
            String[] keyValueSplit = contentDivided[i].split("=");
            values[i] = keyValueSplit[1];
        }
        return values;
    }

    public static DataTools.Data decodeAsData(String fileName){
        String content = ReadWriteFile.readFile(AppUtil.getInstance().getSettingsFile(fileName));
        DataTools.Data output = new DataTools.Data();
        String[] contentDivided = content.split(",");
        for(int i = 0; i< contentDivided.length; i++){
            String[] keyValueSplit = contentDivided[i].split("=");
            output.put(keyValueSplit[0],keyValueSplit[1]);
        }
        return output;
    }

    public static Object[] convertBackToObjects(ObjectTypes[] cipher, String[] decodedRawData){
        if(cipher.length != decodedRawData.length){throw new IllegalArgumentException("Cipher must be the same length as the data array.");}
        Object[] result = new Object[decodedRawData.length];
        for(int i = 0; i < decodedRawData.length; i++){
            switch(cipher[i].value){
                case 1:
                    result[i] = Integer.parseInt(decodedRawData[i]);
                    break;
                case 2:
                    result[i] = Double.parseDouble(decodedRawData[i]);
                    break;
                case 3:
                    result[i] = Boolean.parseBoolean(decodedRawData[i]);
                    break;
                default:
                    result[i] = decodedRawData[i];
                    break;
            }
        }
        return result;
    }
}
