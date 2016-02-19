package io.mycat.ipc;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.Assert;
import org.junit.Test;

import io.mycat.memory.DirectByteBufferPool;
import sun.nio.ch.DirectBuffer;

public class TestDirectByteBufferPool {

    @Test
    public void testAllocate() {
        int pageSize = 1024 * 1024 * 100;
        int allocTimes = 1024 * 1024 * 100;
        DirectByteBufferPool pool = new DirectByteBufferPool(pageSize, (short) 256, (short) 8);
        long start = System.currentTimeMillis();
        for (int i = 0; i < allocTimes; i++) {
            //System.out.println("allocate "+i);
            //long start=System.nanoTime();
            int size = i % (1024 * 8) + 1;
            ByteBuffer byteBufer = pool.allocate(size);
            //System.out.println("alloc "+size+" usage "+(System.nanoTime()-start));
            //start=System.nanoTime();
            pool.recycle(byteBufer);
            //System.out.println("recycle usage "+(System.nanoTime()-start));
        }
        long used = (System.currentTimeMillis() - start);
        System.out.println("total used time  " + used + " avg speed " + allocTimes / used);
    }

    @Test
    public void testAllocateWithDifferentAddress() {
        int size = 256;
        int pageSize = size * 4;
        int allocTimes = 8;
        DirectByteBufferPool pool = new DirectByteBufferPool(pageSize, (short) 256, (short) 2);

        Map<Long, ByteBuffer> buffs = new HashMap<Long, ByteBuffer>(8);
        ByteBuffer byteBuffer = null;
        DirectBuffer directBuffer = null;
        ByteBuffer temp = null;
        long address;
        boolean failure = false;
        for (int i = 0; i < allocTimes; i++) {
            byteBuffer = pool.allocate(size);
            if (byteBuffer == null) {
                Assert.fail("Should have enough memory");
            }
            directBuffer = (DirectBuffer) byteBuffer;
            address = directBuffer.address();
            System.out.println(address);
            temp = buffs.get(address);
            buffs.put(address, byteBuffer);
            if (null != temp) {
                failure = true;
                break;
            }
        }

        for (ByteBuffer buff : buffs.values()) {
            pool.recycle(buff);
        }

        if (failure == true) {
            Assert.fail("Allocate with same address");
        }
    }

    @Test
    public void testAllocateNullWhenOutOfMemory() {
        int size = 256;
        int pageSize = size * 4;
        int allocTimes = 9;
        DirectByteBufferPool pool = new DirectByteBufferPool(pageSize, (short) 256, (short) 2);
        long start = System.currentTimeMillis();
        ByteBuffer byteBuffer = null;
        List<ByteBuffer> buffs = new ArrayList<ByteBuffer>();
        int i = 0;
        for (; i < allocTimes; i++) {
            byteBuffer = pool.allocate(size);
            if (byteBuffer == null) {
                break;
            }
            buffs.add(byteBuffer);
        }
        for (ByteBuffer buff : buffs) {
            pool.recycle(buff);
        }

        Assert.assertEquals("Should out of memory when i = " + 8, i, 8);
    }

    @Test
    public void testAllocateSign() {
        int size = 256;
        int pageSize = size * 4;
        int allocTimes = 9;
        DirectByteBufferPool pool = new DirectByteBufferPool(pageSize, (short) 256, (short) 2);
        long start = System.currentTimeMillis();
        ByteBuffer byteBuffer = null;
        List<ByteBuffer> buffs = new ArrayList<ByteBuffer>();
        int i = 0;
        for (; i < allocTimes; i++) {
            byteBuffer = pool.allocate(size);
            if (byteBuffer == null) {
                break;
            }
            buffs.add(byteBuffer);
        }
        for (ByteBuffer buff : buffs) {
            pool.recycle(buff);
        }

        Assert.assertEquals("Should out of memory when i = " + 8, i, 8);
    }
}
