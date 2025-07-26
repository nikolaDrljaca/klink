package com.drbrosdev.klinkrest.framework;

import com.drbrosdev.klinkrest.domain.klink.EnrichLinkJobExecutor;
import com.drbrosdev.klinkrest.domain.klink.model.EnrichLinkJob;
import com.drbrosdev.klinkrest.domain.klink.usecase.EnrichLink;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

@Log4j2
@Component
@RequiredArgsConstructor
public class EnrichLinkJobExecutorImpl implements EnrichLinkJobExecutor {

    /*
    In memory job queue which processes each 'enrich' job one at a time.
    A single dedicated thread is used.
     */

    private final BlockingQueue<EnrichLinkJob> jobQueue = new LinkedBlockingQueue<>();
    private volatile boolean running = true;

    private final EnrichLink enrichLink;

    private final Runnable processor = () -> {
        while (running) {
            try {
                var job = jobQueue.take();  // will block the current thread until a value is available
                enrichLink.execute(job); // will block until finished
                log.info("Completed enrich job for {}", job.getValue());
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    };

    private final Thread thread = new Thread(processor, "enrich-link-orchestrator");

    /**
     * Thread-safe method to submit jobs for processing.
     */
    @Override
    public void submit(EnrichLinkJob job) {
        log.info("Submitting enrich job for {}", job.getValue());
        jobQueue.offer(job);
    }

    @PostConstruct
    public void start() {
        log.info("Started job orchestrator.");
        thread.setDaemon(true);
        thread.start();
    }

    @PreDestroy
    public void shutdown() {
        thread.interrupt();
        running = false;
        log.info("Stopped job orchestrator.");
    }
}
