package main

deny contains msg if {
  input.kind == "Deployment"
  not input.spec.template.spec.securityContext.runAsNonRoot

  msg := "Containers must not run as root"
}

deny contains msg if {
  input.kind == "Deployment"
  not input.spec.selector.matchLabels.app

  msg := "Containers must provide app label for pod selectors"
}

deny contains msg if {
  input.kind == "Deployment"
  not regex.match(`^.+-[0-9]+\.[0-9]+\.[0-9]+$`, input.metadata.name)
  msg := sprintf("El nombre del Deployment '%s' no termina con una versión semántica", [input.metadata.name])
}

