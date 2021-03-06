package com.zq.dps;

import com.zq.dps.store.RWStore;
import com.zq.dps.store.DataWritable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.EOFException;
import java.io.IOException;
import java.io.Serializable;

/**
 * @program: sword-array
 * @description: swd消息
 * @author: zhouqi1
 * @create: 2019-01-15 20:49
 **/
@Data
@ToString
@NoArgsConstructor
public class DataEntry implements Serializable, DataWritable{

    private static final long serialVersionUID = 2079397312819633699L;

    private Logger logger = LoggerFactory.getLogger(DataEntry.class);
    /**
     * 序列号
     */
    private long seq;

    /**
     * 标签
     */
    private String tag;

    /**
     * 消息体
     */
    private byte[] body;

    /**
     * 时间戳
     */
    private long timestamp;

    @Override
    public long length() {
        return 8 + 4 + ( tag == null ? 0 : tag.length()) + 4 + ( body == null ? 0 : body.length) + 8;
    }

    @Override
    public void read(RWStore store) throws EOFException {
        try{
            seq = store.readLong();

            int tagLen = store.readInt();
            if(tagLen > 0){
                byte[] tagBytes = new byte[tagLen];
                store.read(tagBytes);
                tag = new String(tagBytes);
            }

            int len = store.readInt();
            if(len > 0){
                byte[] bodyBytes = new byte[len];
                store.read(bodyBytes);
                body = bodyBytes;
            }
            timestamp = store.readLong();
        }catch (EOFException e){
            throw e;
        }catch (IOException e){
            logger.error("写入文件错误", e);
        }

    }

    @Override
    public void write(RWStore store) {
        try{
            store.writeLong(seq);

            store.writeInt(tag.length());
            if(tag.length() > 0){
                store.write(tag.getBytes());
            }
            store.writeInt(body.length);
            if(body.length > 0){
                store.write(body);
            }
            store.writeLong(timestamp);
        }catch (IOException e){
            logger.error("写入文件错误", e);
        }
    }
}
