{{- /* Prints common labels */ -}}
{{- define "k8s-mongo.printSharedLabels" -}}
helm.sh/chart: {{ .Chart.Name }}-{{ .Chart.Version | replace "+" "_" }}
app.kubernetes.io/name: {{ .Release.Name }}-k8s-mongo
app.kubernetes.io/managed-by: {{ .Release.Service }}
app.kubernetes.io/instance: {{ sha256sum (printf "%s-%s" .Values.domain .Release.Name) }}
app.kubernetes.io/version: {{ .Chart.AppVersion }}
app.kubernetes.io/component: k8s-mongo
app.kubernetes.io/part-of: {{ .Release.Name }}
{{- end -}}