#!/bin/bash

HELM_REPO_NAME="raspi"
HELM_REPO_URL="https://irdcat.github.io/raspi/"

usage() {
    echo "Usage:"
    echo "   $0 --from-manifest <manifest-file>                       # Install components from YAML manifest"
    echo "   $0 --name <component_name> --version <component_version> # Install single component"
    exit 1
}

parse_yaml() {
    local yaml_file="$1"
    local name=""
    local version=""

    if ! grep -q "^components:" "$yaml_file"; then
        echo "Error: YAML manifest must contain a 'components' field."
        exit 1
    fi

    while read -r line; do
        line=$(echo "$line" | xargs)

        if [[ "$line" == "- name:"* ]]; then
            name=$(echo "$line" | awk -F": " '{print $2}')
        elif [[ "$line" == "version:"* ]]; then
            version=$(echo "$line" | awk -F": " '{print $2}')
            if [[ -n "$name" && -n "$version" ]]; then
                echo "$name $version"
                name=""
                version=""
            fi
        fi
    done < "$yaml_file" 
}

install_component() {
    local name="$1"
    local version="$2"
    echo "Installing $name (Version: $version)"
    helm upgrade \
        --install "$name" "$HELM_REPO_NAME/$name" \
        --version "$version" \ 
        --namespace default \
        --wait

    if [[ $? -eq 0 ]]; then
        echo "✅ Successfully installed $name"
    else
        echo "❌ Failed to install $name"
    fi
}

update_helm_repositories() {
    if ! helm repo list | grep -q "$HELM_REPO_NAME"; then
        echo "Adding Helm repository: $HELM_REPO_URL..."
        helm repo add "$HELM_REPO_NAME" "$HELM_REPO_URL"
    fi
    helm repo update
}

# Command line arguments
manifest_file=""
component_name=""
component_version=""

while [[ $# -gt 0 ]]; do
    case "$1" in
        --from-manifest)
            manifest_file="$2"
            shift 2
            ;;
        --name)
            component_name="$2"
            shift 2
            ;;
        --version)
            component_version="$2"
            shift 2
            ;;
        *)
            usage
            ;;
    esac
done

# Validate arguments
if [[ -n "$manifest_file" && (-n "$component_name" || -n "$component_version") ]]; then
    echo "Error: --from-manifest cannot be used with --name or --version"
    usage
elif [[ -n "$component_name" && -z "$component_version" ]]; then
    echo "Error: --name requires --version"
    usage
elif [[ -n "$component_version" && -z "$component_name" ]]; then
    echo "Error: --version requires --name"
    usage
elif [[ -z "$manifest_file" && -z "$component_name" && -z "$component_version" ]]; then
    echo "Error: No installation option provided"
    usage
fi

update_helm_repositories

if [[ -n "$manifest_file" ]]; then
    while read -r name version; do
        install_component "$name" "$version"
    done < <(parse_yaml "$manifest_file")
fi

if [[ -n "$component_name" && -n "$component_version" ]]; then
    install_component "$component_name" "$component_version"
fi