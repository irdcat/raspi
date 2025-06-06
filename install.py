import argparse
import subprocess
from dataclasses import dataclass
import yaml
import sys

HELM_REPO_NAME="raspi"
HELM_REPO_URL="https://irdcat.github.io/raspi/"

@dataclass
class Component:
    name: str
    version: str

def parse_yaml(filename: str) -> list[Component]:
    with open(filename, 'r') as file:
        data = yaml.safe_load(file)
    return [Component(name=item['name'], version=item['version']) for item in data['components']]

def install_component(component: Component):
    print("Installing {name} (Version: {version})".format(
        name = component.name,
        version = component.version
    ))
    upgrade = subprocess.run([
        "helm", "upgrade",
        "--install", component.name, "{repo}/{name}".format(repo=HELM_REPO_NAME, name=component.name),
        "--version", component.version,
        "--namespace", "default",
        "--wait",
        "--atomic"])
    if (upgrade.returncode == 0):
        print("✅ Successfully installed {name}".format(name=component.name))
    else:
        print("❌ Failed to install {name}".format(name=component.name))

def helm_repository_exists(name: str) -> bool:
    exists = subprocess.run([
        "helm", "repo", "list", "|",
        "grep", "-q", name
    ])
    return exists.returncode == 0

def helm_repository_add(name: str, url: str):
    add = subprocess.run([
        "helm", "repo", "add", name, url
    ])

def update_helm_repositories():
    if not helm_repository_exists(HELM_REPO_NAME):
        helm_repository_add(HELM_REPO_NAME, HELM_REPO_URL)
    subprocess.run(["helm", "repo", "update"])


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
args.remove('install.py')

parsed = parser.parse_args(args)
action = parsed.action

update_helm_repositories()
if (action == 'single'):
    component = Component(parsed.name, parsed.version)
    install_component(component)
elif (action == 'manifest'):
    components = parse_yaml(parsed.manifest_file)
    for component in components:
        install_component(component)
else:
    print("Unknown command " + action)