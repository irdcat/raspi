{{- /* Prints common labels */ -}}
{{- define "mongo-ui.printSharedLabels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
app.kubernetes.io/name: {{ .Release.Name }}-mongo-ui
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ substr 0 63 (sha256sum (printf "%s-%s" .Values.domain .Release.Name)) }}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
app.kubernetes.io/component: mongo-ui
app.kubernetes.io/part-of: {{ .Release.Name }}
{{- end -}}