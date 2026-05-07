package com.vforce360.config;

import com.vforce360.adapters.FlexmarkRenderingAdapter;
import com.vforce360.ports.MarkdownRendererPort;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration for Report generation dependencies.
 * Wires up the Markdown renderer implementation.
 */
@Configuration
public class ReportRepositoryConfig {

    @Bean
    public MarkdownRendererPort markdownRendererAdapter() {
        // Explicitly returning the interface type, implemented by Flexmark
        return new FlexmarkRenderingAdapter();
    }
}
