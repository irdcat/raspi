import argparse
import os
import subprocess
from dataclasses import dataclass
import yaml
import sys

HELM_REPO_NAME="raspi"
HELM_REPO_URL="https://irdcat.github.io/raspi/"

@dataclass
class HelmRepository:
    name: str
    url: str

@dataclass
class Component:
    name: str
    version: str
    config: any

@dataclass
class InstalledComponent:
    chart: str
    namespace: str

def parse_installed(yaml_string: str) -> list[InstalledComponent]:
    data = yaml.safe_load(yaml_string)
    return [InstalledComponent(chart=item['chart'], namespace=item['namespace']) for item in data]

def parse_manifest(filename: str) -> list[Component]:
    with open(filename, 'r') as file:
        data = yaml.safe_load(file)

    components: list[Component] = []
    for item in data.get('components', []):
        try:
            config = item.get('config')
        except:
            config = None
        components.append(Component(
            name=item.get('name'), 
            version=item.get('version'),
            config=config
        ))

    return components

def parse_helm_repositories(yaml_string: str) -> list[HelmRepository]:
    data = yaml.safe_load(yaml_string)
    return [HelmRepository(name=item['name'], url=item['url']) for item in data]

def component_install(component: Component):
    if (component_installed(component)):
        print("{name} {version} is already installed. Aborting.".format(name=component.name, version=component.version))
        return
    
    print("Installing {name} (Version: {version})".format(
        name = component.name,
        version = component.version
    ))

    component_config_save(component)

    exe_with_args = [
        "helm", "upgrade",
        "--install", component.name, "{repo}/{name}".format(repo=HELM_REPO_NAME, name=component.name),
        "--version", component.version,
        "--namespace", "default",
        "--wait",
        "--atomic"
    ]
    
    if (component.config is not None):
        exe_with_args.append("-f")
        exe_with_args.append(f"__{component.name}_config.yaml")

    upgrade = subprocess.run(exe_with_args)
    component_config_cleanup(component)
    
    if (upgrade.returncode == 0):
        print("✅ Successfully installed {name}".format(name=component.name))
    else:
        print("❌ Failed to install {name}".format(name=component.name))

def component_config_save(component: Component):
    if (component.config is None):
        return
    
    filename = f"__{component.name}_config.yaml"
    
    with open(filename, 'w') as config_file:
        yaml.dump(component.config, config_file, default_flow_style=False)

def component_config_cleanup(component: Component):
    if (component.config is None):
        return
    
    filename = f"__{component.name}_config.yaml"

    try:
        os.remove(filename)
        print(f"Cleanup of {component.name} temparary data is done!")
    except FileNotFoundError:
        print(f"Attempt to cleanup {component.name} temporary data that was not created.")
    except PermissionError:
        print(f"Unsufficient permissions to cleanup {component.name} temporary data.")
    except Exception as e:
        print(f"Unknown error while removing {component.name} tempoarary data.")

def component_installed(component: Component) -> bool:
    output = subprocess.check_output(["helm", "list", "-o", "yaml"])
    installed_components = parse_installed(output)
    checked_component_chart = "{name}-{version}".format(name=component.name, version=component.version)
    for installed_component in installed_components:
        if installed_component.chart == checked_component_chart:
            return True
    return False

def helm_repository_exists(name: str) -> bool:
    output = subprocess.check_output(["helm", "repo", "list", "-o", "yaml"])
    helm_repositories = parse_helm_repositories(output)
    for helm_repository in helm_repositories:
        if helm_repository.name == name:
            return True
    return False

def helm_repository_add(name: str, url: str):
    subprocess.run(["helm", "repo", "add", name, url])

def update_helm_repositories():
    if not helm_repository_exists(HELM_REPO_NAME):
        helm_repository_add(HELM_REPO_NAME, HELM_REPO_URL)
    subprocess.run(["helm", "repo", "update"], stdout=open(os.devnull, 'wb'));


parser = argparse.ArgumentParser(
    prog='Raspi Install Script',
    description='Installation script for Raspi project')

sub_parsers = parser.add_subparsers(
    dest='action',
    metavar='command')

single_component_subparser = sub_parsers.add_parser(
    'single', 
    help='Install single component')
single_component_subparser.add_argument(
    'name',
    help='Name of the component to be installed')
single_component_subparser.add_argument(
    'version',
    help='Version of the component to be installed')

manifest_install_subparser = sub_parsers.add_parser(
    'manifest', 
    help='Install 1 or more components based on provided manifest')
manifest_install_subparser.add_argument(
    'manifest_file',
    help='Manifest defined in a YAML format')

args = sys.argv
args.remove('./scripts/install.py')

parsed = parser.parse_args(args)
action = parsed.action

update_helm_repositories()
if (action == 'single'):
    component = Component(parsed.name, parsed.version)
    component_install(component)
elif (action == 'manifest'):
    components = parse_manifest(parsed.manifest_file)
    for component in components:
        component_install(component)
else:
    print("Unknown command " + action)