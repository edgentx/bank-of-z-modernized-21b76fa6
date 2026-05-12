{{/*
Common helpers for the teller chart.
*/}}

{{- define "teller.fullname" -}}
{{- printf "%s" .Release.Name | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{- define "teller.chart" -}}
{{- printf "%s-%s" .Chart.Name .Chart.Version | replace "+" "_" | trunc 63 | trimSuffix "-" -}}
{{- end -}}

{{/*
Common labels applied to every object.
Pass the calling component name as the dict via `dict "ctx" . "component" "backend"`.
*/}}
{{- define "teller.labels" -}}
helm.sh/chart: {{ include "teller.chart" .ctx }}
app.kubernetes.io/name: {{ .component }}
app.kubernetes.io/instance: {{ .ctx.Release.Name }}
app.kubernetes.io/version: {{ .ctx.Chart.AppVersion | quote }}
app.kubernetes.io/managed-by: {{ .ctx.Release.Service }}
app.kubernetes.io/part-of: bank-of-z
{{- with .ctx.Values.global.commonLabels }}
{{ toYaml . }}
{{- end }}
{{- end -}}

{{/*
Selector labels — must be stable across upgrades.
*/}}
{{- define "teller.selectorLabels" -}}
app.kubernetes.io/name: {{ .component }}
app.kubernetes.io/instance: {{ .ctx.Release.Name }}
{{- end -}}

{{/*
Fully-qualified image reference for a given image spec.
Call as: include "teller.image" (dict "global" .Values.global "image" .Values.backend.image)
*/}}
{{- define "teller.image" -}}
{{- $registry := .global.imageRegistry -}}
{{- $repo := .global.imageRepository -}}
{{- $name := .image.name -}}
{{- $tag := .image.tag | default "latest" -}}
{{- printf "%s/%s/%s:%s" $registry $repo $name $tag -}}
{{- end -}}

{{/*
Resolved image pull policy: per-image override falls back to the global.
*/}}
{{- define "teller.pullPolicy" -}}
{{- if .image.pullPolicy -}}{{ .image.pullPolicy }}{{- else -}}{{ .global.imagePullPolicy }}{{- end -}}
{{- end -}}

{{/*
Names of the rendered ConfigMap and Secret resources.
*/}}
{{- define "teller.backend.configmapName" -}}
{{ include "teller.fullname" . }}-backend-config
{{- end -}}

{{- define "teller.backend.secretName" -}}
{{ include "teller.fullname" . }}-backend-secret
{{- end -}}

{{- define "teller.envoy.configmapName" -}}
{{ include "teller.fullname" . }}-envoy-config
{{- end -}}

{{- define "teller.opa.configmapName" -}}
{{ include "teller.fullname" . }}-opa-config
{{- end -}}
