package com.rbiedrawa.app.web;

import static java.util.stream.Collectors.toList;

import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import org.springframework.context.annotation.Profile;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.config.StreamsBuilderFactoryBean;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.streams.StreamsConfig;
import org.apache.kafka.streams.Topology;
import org.apache.kafka.streams.TopologyDescription;

@Profile({"test", "local"})
@Slf4j
@RestController
@RequestMapping("topologies")
@RequiredArgsConstructor
public class TopologyController {
	private final List<StreamsBuilderFactoryBean> streamsBuilders;

	@GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
	ResponseEntity<Map<String, List<String>>> findAll() {
		List<String> applicationIds = streamsBuilders.stream()
													 .map(StreamsBuilderFactoryBean::getStreamsConfiguration)
													 .filter(Objects::nonNull)
													 .map(config -> config.get(StreamsConfig.APPLICATION_ID_CONFIG))
													 .filter(Objects::nonNull)
													 .map(String.class::cast)
													 .collect(toList());
		return ResponseEntity.ok(Map.of("topologies", applicationIds));
	}

	@GetMapping(value = "{applicationId}", produces = MediaType.TEXT_PLAIN_VALUE)
	ResponseEntity<String> findTopology(@PathVariable String applicationId) {
		return findByApplicationId(applicationId).map(StreamsBuilderFactoryBean::getTopology)
												 .map(Topology::describe)
												 .map(TopologyDescription::toString)
												 .map(ResponseEntity::ok)
												 .orElse(ResponseEntity.notFound().build());
	}

	private Optional<StreamsBuilderFactoryBean> findByApplicationId(String applicationId) {
		return streamsBuilders.stream()
							  .filter(streamsBuilderFactoryBean -> {
								  var streamsConfiguration = streamsBuilderFactoryBean.getStreamsConfiguration();
								  return streamsConfiguration != null && applicationId.equals(streamsConfiguration.get(StreamsConfig.APPLICATION_ID_CONFIG));
							  })
							  .findFirst();
	}

}
