package id.ac.ui.cs.advprog.review;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvBuilder;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class ReviewApplicationTests {

    private MockedStatic<SpringApplication> springApplicationMock;
    private MockedStatic<Dotenv> dotenvMock;
    private ConfigurableApplicationContext contextMock;
    private Dotenv dotenvInstanceMock;
    private DotenvBuilder dotenvBuilderMock;

    @BeforeEach
    void setUp() {
        springApplicationMock = mockStatic(SpringApplication.class);
        dotenvMock = mockStatic(Dotenv.class);
        contextMock = mock(ConfigurableApplicationContext.class);
        dotenvInstanceMock = mock(Dotenv.class);
        dotenvBuilderMock = mock(DotenvBuilder.class);
    }

    @AfterEach
    void tearDown() {
        springApplicationMock.close();
        dotenvMock.close();

        System.clearProperty("TEST_KEY");
        System.clearProperty("ANOTHER_KEY");
    }

    @Test
    void main_shouldRunSuccessfully() {

        String[] args = {"arg1", "arg2"};

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);

        DotenvEntry entry1 = mock(DotenvEntry.class);
        DotenvEntry entry2 = mock(DotenvEntry.class);
        when(entry1.getKey()).thenReturn("TEST_KEY");
        when(entry1.getValue()).thenReturn("test_value");
        when(entry2.getKey()).thenReturn("ANOTHER_KEY");
        when(entry2.getValue()).thenReturn("another_value");

        Set<DotenvEntry> entries = Set.of(entry1, entry2);
        when(dotenvInstanceMock.entries()).thenReturn(entries);

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, args))
                .thenReturn(contextMock);

        assertDoesNotThrow(() -> ReviewApplication.main(args));

        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, args));
        dotenvMock.verify(Dotenv::configure);
        verify(dotenvBuilderMock).ignoreIfMissing();
        verify(dotenvBuilderMock).load();
        verify(dotenvInstanceMock).entries();

        assertEquals("test_value", System.getProperty("TEST_KEY"));
        assertEquals("another_value", System.getProperty("ANOTHER_KEY"));
    }

    @Test
    void main_shouldHandleEmptyArgs() {

        String[] emptyArgs = {};

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);
        when(dotenvInstanceMock.entries()).thenReturn(Set.of());

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, emptyArgs))
                .thenReturn(contextMock);

        assertDoesNotThrow(() -> ReviewApplication.main(emptyArgs));

        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, emptyArgs));
    }

    @Test
    void main_shouldHandleNullArgs() {

        String[] nullArgs = null;

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);
        when(dotenvInstanceMock.entries()).thenReturn(Set.of());

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, nullArgs))
                .thenReturn(contextMock);

        assertDoesNotThrow(() -> ReviewApplication.main(nullArgs));

        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, nullArgs));
    }

    @Test
    void main_shouldHandleEmptyDotenvEntries() {

        String[] args = {"test"};

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);
        when(dotenvInstanceMock.entries()).thenReturn(Set.of());

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, args))
                .thenReturn(contextMock);

        assertDoesNotThrow(() -> ReviewApplication.main(args));

        verify(dotenvInstanceMock).entries();
        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, args));
    }

    @Test
    void main_shouldHandleDotenvWithMultipleEntries() {

        String[] args = {"test"};

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);

        DotenvEntry entry1 = mock(DotenvEntry.class);
        DotenvEntry entry2 = mock(DotenvEntry.class);
        DotenvEntry entry3 = mock(DotenvEntry.class);

        when(entry1.getKey()).thenReturn("DB_HOST");
        when(entry1.getValue()).thenReturn("localhost");
        when(entry2.getKey()).thenReturn("DB_PORT");
        when(entry2.getValue()).thenReturn("5432");
        when(entry3.getKey()).thenReturn("DB_NAME");
        when(entry3.getValue()).thenReturn("eventsphere");

        Set<DotenvEntry> entries = Set.of(entry1, entry2, entry3);
        when(dotenvInstanceMock.entries()).thenReturn(entries);

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, args))
                .thenReturn(contextMock);

        assertDoesNotThrow(() -> ReviewApplication.main(args));

        verify(dotenvInstanceMock).entries();
        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, args));

        assertEquals("localhost", System.getProperty("DB_HOST"));
        assertEquals("5432", System.getProperty("DB_PORT"));
        assertEquals("eventsphere", System.getProperty("DB_NAME"));
    }

    @Test
    void main_shouldHandleSpringApplicationException() {

        String[] args = {"test"};

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);
        when(dotenvInstanceMock.entries()).thenReturn(Set.of());

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, args))
                .thenThrow(new RuntimeException("Spring application failed to start"));

        assertThrows(RuntimeException.class, () -> ReviewApplication.main(args));

        verify(dotenvInstanceMock).entries();
        springApplicationMock.verify(() -> SpringApplication.run(ReviewApplication.class, args));
    }

    @Test
    void constructor_shouldCreateInstance() {

        assertDoesNotThrow(() -> new ReviewApplication());
    }

    @Test
    void main_shouldSetSystemPropertiesCorrectly() {

        String[] args = {};

        System.clearProperty("CUSTOM_PROP");

        dotenvMock.when(Dotenv::configure).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.ignoreIfMissing()).thenReturn(dotenvBuilderMock);
        when(dotenvBuilderMock.load()).thenReturn(dotenvInstanceMock);

        DotenvEntry entry = mock(DotenvEntry.class);
        when(entry.getKey()).thenReturn("CUSTOM_PROP");
        when(entry.getValue()).thenReturn("custom_value");
        when(dotenvInstanceMock.entries()).thenReturn(Set.of(entry));

        springApplicationMock.when(() -> SpringApplication.run(ReviewApplication.class, args))
                .thenReturn(contextMock);

        ReviewApplication.main(args);

        assertEquals("custom_value", System.getProperty("CUSTOM_PROP"));

        System.clearProperty("CUSTOM_PROP");
    }
}