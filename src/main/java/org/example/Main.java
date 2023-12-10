package org.example;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    private static int _totalNumberCalls = 0;
    private static final int API_LIMIT = 290;
    private static final String TICKER = "AAPL";
    private static final int NUMBER_OF_ASYNC_CALLS = 5;

    public static void main(String[] args) {
        List<String> unfetchedTickers = new ArrayList<>();
        List<String> fakeListOfTickers = getFakeListOfTickers();

        //make 5 async calls based on declared API limit
        for (int i = 0; i < NUMBER_OF_ASYNC_CALLS; i++) {
            AtomicInteger successfulRetrievalCount = new AtomicInteger();
            AtomicInteger failedRetrievalCount = new AtomicInteger();
            long startTime = System.nanoTime();
            System.out.println("-----------\nFetching data for bulk number " + i + " at " + LocalDateTime.now() + "\n------------");

            List<CompletableFuture<String>> futures = new ArrayList<>();

            for (String currentTicker : fakeListOfTickers) {
                sleepInSeconds(0.1);
                int totalCount = _totalNumberCalls++;
                //async request
                futures.add(CompletableFuture.supplyAsync(() -> {
                    try {
                        JsonApiInformation jsonApiInformation = new JsonApiInformation.Builder()
                                .setUrl("https://financialmodelingprep.com/api/v3/historical-chart/15min/" + currentTicker + "?apikey=" + FmpApiKey.KEY + "&extended=true")
                                .build();
                        String jsonResponse = JsonRetrieverUrl.getJsonResponse(jsonApiInformation);
                        System.out.println("Successfully retrieved data for " + currentTicker + ", at:" + LocalTime.now() + ". API call # " + totalCount);
                        successfulRetrievalCount.getAndIncrement();
                    } catch (Exception e) {
                        System.out.println("Unable to get the 15 min pre/post data for " + currentTicker + ", at:" + LocalTime.now() + " " + e.getMessage() + ". API call # " + totalCount);
                        unfetchedTickers.add(currentTicker);
                        failedRetrievalCount.getAndIncrement();
                    }
                    return "Completed retrieval";
                }));
            }

            for (CompletableFuture<String> future : futures) {
                future.join(); // wait for each
            }

            long endTime = System.nanoTime();
            long elapsedTime = TimeUnit.SECONDS.convert(endTime - startTime, TimeUnit.NANOSECONDS);
            double retirevalRate = successfulRetrievalCount.get() / (double) (successfulRetrievalCount.get() + failedRetrievalCount.get()) * 100;
            System.out.println("------\nFetched data for bulk number " + i + " at " + LocalDateTime.now() +
                    " Successful retrieval rate: " + Math.round(retirevalRate) + "% (success: " + successfulRetrievalCount + " || fail: " + failedRetrievalCount + ")" +
                    ". It took a total of " + elapsedTime + " seconds. Sleep for one minute now\n------------");
            sleepInSeconds(60);
        }

        System.out.println("Total number of unfetched data: " + unfetchedTickers.size() + " out of " + fakeListOfTickers.size() * NUMBER_OF_ASYNC_CALLS);
    }

    private static List<String> getFakeListOfTickers() {
        List<String> fakeList = new ArrayList<>();
        for (int i = 0; i < API_LIMIT; i++) {
            fakeList.add(TICKER);
        }
        return fakeList;
    }

    private static void sleepInSeconds(double seconds) {
        try {
            double milliSeconds = seconds * 1000L;
            Thread.sleep((long) milliSeconds);
        } catch (InterruptedException ignore) {
        }
    }
}