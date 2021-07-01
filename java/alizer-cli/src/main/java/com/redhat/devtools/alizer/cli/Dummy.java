package com.redhat.devtools.alizer.cli;

import com.redhat.devtools.alizer.registry.support.DevfileMetadata;
import io.quarkus.runtime.annotations.RegisterForReflection;

@RegisterForReflection(targets = DevfileMetadata.class)
public class Dummy {
}
