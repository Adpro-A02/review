package id.ac.ui.cs.advprog.review.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class AsyncConfigTest {

    @InjectMocks
    private AsyncConfig asyncConfig;

    @Test
    void testTaskExecutorBean() {

        Executor executor = asyncConfig.taskExecutor();

        assertNotNull(executor, "Executor bean should not be null.");
        assertTrue(executor instanceof ThreadPoolTaskExecutor, "Executor should be an instance of ThreadPoolTaskExecutor.");

        ThreadPoolTaskExecutor threadPoolTaskExecutor = (ThreadPoolTaskExecutor) executor;

        assertEquals(5, threadPoolTaskExecutor.getCorePoolSize(), "Core pool size should be 5.");
        assertEquals(10, threadPoolTaskExecutor.getMaxPoolSize(), "Max pool size should be 10.");
        assertEquals(25, threadPoolTaskExecutor.getQueueCapacity(), "Queue capacity should be 25.");
        assertEquals("AsyncExecutor-", threadPoolTaskExecutor.getThreadNamePrefix(), "Thread name prefix should match.");

    }
}