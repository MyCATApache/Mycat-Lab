package io.mycat.memory;

import java.nio.ByteBuffer;

import sun.nio.ch.DirectBuffer;

/**
 * DirectByteBuffer池，可以分配任意指定大小的DirectByteBuffer，用完需要归还
 *
 * @author wuzhih
 */
@SuppressWarnings("restriction")
public class DirectByteBufferPool {

    private ByteBufferPage[] allPages;
    private final int chunkSize;
    private int prevAllocatedPage = 0;

    public DirectByteBufferPool(int pageSize, short chunkSize, short pageCount) {
        allPages = new ByteBufferPage[pageCount];
        this.chunkSize = chunkSize;
        for (int i = 0; i < pageCount; i++) {
            allPages[i] = new ByteBufferPage(ByteBuffer.allocateDirect(pageSize), chunkSize);
        }
    }

    public ByteBuffer allocate(int size) {
        int theChunkCount = size / chunkSize + (size % chunkSize == 0 ? 0 : 1);
        int selectedPage = (++prevAllocatedPage) % allPages.length;
        ByteBuffer byteBuf = allocateBuffer(theChunkCount, 0, selectedPage);
        if (byteBuf == null) {
            byteBuf = allocateBuffer(theChunkCount, selectedPage, allPages.length);
        }
        return byteBuf;
    }

    public void recycle(ByteBuffer theBuf) {
        boolean recycled = false;
        sun.nio.ch.DirectBuffer thisNavBuf = (DirectBuffer) theBuf;
        int chunkCount = theBuf.capacity() / chunkSize;
        sun.nio.ch.DirectBuffer parentBuf = (DirectBuffer) thisNavBuf.attachment();
        int startChunk = (int) ((thisNavBuf.address() - parentBuf.address()) / this.chunkSize);
        for (int i = 0; i < allPages.length; i++) {
            if ((recycled = allPages[i].recycleBuffer((ByteBuffer) parentBuf, startChunk, chunkCount) == true)) {
                break;
            }
        }
        if (recycled == false) {
            System.out.println("warning ,not recycled buffer " + theBuf);
        }
    }

    private ByteBuffer allocateBuffer(int theChunkCount, int startPage, int endPage) {
        for (int i = startPage; i < endPage; i++) {
            ByteBuffer buffer = allPages[i].allocatChunk(theChunkCount);
            if (buffer != null) {
                prevAllocatedPage = i;
                return buffer;
            }
        }
        return null;
    }
}
