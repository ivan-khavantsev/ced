package org.meetla.ced.model;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Base64;

public class Message {
    public static byte TYPE = 1;

    public String e; // Encrypted message
    public String n; // Nonce

    public byte[] cipherData;
    public byte[] nonce;

    public String encode(){
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            baos.write(new byte[]{TYPE});
            baos.write(ByteBuffer.allocate(4).putInt(cipherData.length).array());
            baos.write(cipherData);
            baos.write(ByteBuffer.allocate(4).putInt(nonce.length).array());
            baos.write(nonce);
            return new String(Base64.getEncoder().encode(baos.toByteArray()));
        } catch (Throwable t){
            System.out.println(t.getMessage());
        }
        return null;
    }

    public static Message decode(String encoded){
        try{

        byte[] data = Base64.getDecoder().decode(encoded);
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        byte[] type = bais.readNBytes(1);
        byte[] lengthBytes = bais.readNBytes(4);
        int length = ByteBuffer.wrap(lengthBytes).getInt();
        Message message = new Message();
        message.cipherData = bais.readNBytes(length);
        lengthBytes = bais.readNBytes(4);
        length = ByteBuffer.wrap(lengthBytes).getInt();
        message.nonce = bais.readNBytes(length);
        return message;
        } catch (Throwable t){
            System.out.println(t.getMessage());
        }
        return null;
    }
}
