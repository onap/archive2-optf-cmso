package org.onap.optf.cmso;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;


@Configuration
@PropertySources({
	@PropertySource("file:etc/config/cmso.properties"),
	@PropertySource("file:etc/config/optimizer.properties"),
	@PropertySource("file:etc/config/ticketmgt.properties"),
})
public class ApplicationPropertiesFiles 
{
}