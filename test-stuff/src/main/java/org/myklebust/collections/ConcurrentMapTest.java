package org.myklebust.collections;

import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ConcurrentMapTest
{
    final static int OBJECTS = 250000;

    final static int ITERATIONS = 5;

    final static int THREADPOOL = 10;


    public static void main( String... args )
        throws Exception
    {
        //runTest( Maps.newHashMap() );
        // runTest( Maps.newLinkedHashMap() );

        runTest( new Hashtable<>() );
        runTest( Collections.synchronizedMap( new HashMap<>() ) );
        runTest( new ConcurrentHashMap<>( new HashMap<>() ) );
    }

    private static void runTest( final Map<String, Integer> hasMap )
        throws Exception
    {
        System.out.println( "Test started for: " + hasMap.getClass() );

        int avgTime = 0;

        for ( int i = 0; i < ITERATIONS; i++ )
        {
            final long start = System.nanoTime();

            final ExecutorService executorservice = Executors.newFixedThreadPool( THREADPOOL );

            for ( int j = 0; j < THREADPOOL; j++ )
            {
                executorservice.execute( new Executor( OBJECTS, hasMap ) );
            }

            executorservice.shutdown();
            executorservice.awaitTermination( Long.MAX_VALUE, TimeUnit.DAYS );

            final long stop = System.nanoTime();
            long totalTime = ( stop - start ) / 1000000L;

            System.out.println( "Added/fetched " + OBJECTS + " in : " + totalTime + " ms" );

            avgTime += totalTime;
        }

        System.out.println( "Average time: " + avgTime / ITERATIONS + " ms" );
    }

    private static class Executor
        implements Runnable
    {
        private final int numObjects;

        private Map<String, Integer> map;

        public Executor( final int numObjects, final Map<String, Integer> map )
        {
            this.numObjects = numObjects;
            this.map = map;
        }

        @Override
        public void run()
        {
            for ( int i = 0; i < numObjects; i++ )
            {
                final Integer value = (int) Math.ceil( Math.random() * 550000 );
                final String key = String.valueOf( value );
                map.get( value );
                map.put( key, value );
            }
        }
    }
}
