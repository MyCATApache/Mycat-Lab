package io.mycat.ipc;

import java.nio.ByteBuffer;

import org.junit.Test;

import io.mycat.memory.DirectByteBufferPool;

public class TestDirectByteBufferPool {

	@Test
	public void testAllocate()
	{
		int pageSize=1024*1024*100;
		int allocTimes=1024*1024*100;
		DirectByteBufferPool pool=new DirectByteBufferPool(pageSize,(short)256,(short) 8);
		long start=System.currentTimeMillis();
		for(int i=0;i<allocTimes;i++)
		{
			//System.out.println("allocat "+i);
			//long start=System.nanoTime();
			int size=i%(1024*8)+1;
			ByteBuffer byteBufer=pool.allocat(size);
			//System.out.println("alloc "+size+" usage "+(System.nanoTime()-start));
			//start=System.nanoTime();
			pool.recycle(byteBufer);	
			//System.out.println("recycle usage "+(System.nanoTime()-start));
		}
		long used=(System.currentTimeMillis()-start);
		System.out.println("total used time  "+used+ " avg speed "+allocTimes/used);
	}
}
