import sys
import os
import re
import shutil

VALID_PROJECT_NAME = re.compile("^[A-Z][a-z]+$")

def valid_project_name(project_name):
    return VALID_PROJECT_NAME.match(project_name) != None

def replace_text_in_file(file_name, lookup_text, replace_text):
    outfile = file_name + ".out"
    with open(outfile, "w") as out:
        with open(file_name, "r") as infile:
            for line in infile:
                out.write(line.replace(lookup_text, replace_text))
        
    os.remove(file_name)
    os.rename(outfile, file_name)
    
def ignored_files(adir, filenames):
    return [filename for filename in filenames if filename == ".git"]
    
project_name = ""
while (not valid_project_name(project_name)):
    project_name = raw_input("Project name (capital followed by lowercase letters): ")

package_name = project_name.lower()
    
# The new top level directory 
new_project_dir = os.path.join(os.path.split(sys.path[0])[0], project_name + "LibgdxProject")

# Copy the previous contents across to the new project directory
shutil.copytree(sys.path[0], new_project_dir, ignore = ignored_files)
            
# .project files
replace_text_in_file(os.path.join(new_project_dir, "LibGdxAndroidProject", ".project"),
                     "LibgdxAndroidProject",
                     project_name + "AndroidProject")
replace_text_in_file(os.path.join(new_project_dir, "LibGdxDesktopProject", ".project"),
                     "LibgdxDesktopProject",
                     project_name + "DesktopProject")
replace_text_in_file(os.path.join(new_project_dir, "LibGdxDesktopProject", ".project"),
                     "C:/Home Projects/LibgdxTemplateProjects/LibgdxAndroidProject/assets",
                     new_project_dir.replace("\\", "/") + "/" + project_name + "AndroidProject/assets")
replace_text_in_file(os.path.join(new_project_dir, "LibGdxCoreProject", ".project"),
                     "LibgdxCoreProject",
                     project_name + "CoreProject")
                     
# Android Manifest
replace_text_in_file(os.path.join(new_project_dir, "LibGdxAndroidProject", "AndroidManifest.xml"),
                     "net.tyler.applicationname",
                     "net.tyler." + package_name)
                     
# Classpaths
replace_text_in_file(os.path.join(new_project_dir, "LibGdxAndroidProject", ".classpath"),
                     "LibgdxCoreProject",
                     project_name + "CoreProject")
replace_text_in_file(os.path.join(new_project_dir, "LibGdxDesktopProject", ".classpath"),
                     "LibgdxCoreProject",
                     project_name + "CoreProject")
                     
# Main scala application class
os.rename(os.path.join(new_project_dir, "LibgdxCoreProject", "src", "net", "tyler", "applicationname", "TemplateApplicationGame.scala"),
          os.path.join(new_project_dir, "LibgdxCoreProject", "src", "net", "tyler", "applicationname", project_name + "Game.scala"))

os.rename(os.path.join(new_project_dir, "LibgdxDesktopProject", "src", "net", "tyler", "applicationname", "TemplateApplicationDesktop.java"),
          os.path.join(new_project_dir, "LibgdxDesktopProject", "src", "net", "tyler", "applicationname", project_name + "Desktop.java"))
          
subdirs_to_modify = []        
# Package names
for root, dirs, files in os.walk(new_project_dir):
    for file in files:
        if file.endswith(".java") or file.endswith(".scala"):
            replace_text_in_file(os.path.join(root, file), "applicationname", package_name)
            replace_text_in_file(os.path.join(root, file), "TemplateApplication", project_name)
    
    for dir in dirs:
        if dir.endswith("applicationname"):
            subdirs_to_modify.append(os.path.join(root, dir))

# Can't modify whilst iterating over them using walk. Do it now instead.
for dir in subdirs_to_modify:
    shutil.move(dir, os.path.join(os.path.split(dir)[0], package_name))
    
# Rename project directories
shutil.move(os.path.join(new_project_dir, "LibGdxAndroidProject"), 
            os.path.join(new_project_dir, project_name + "AndroidProject"))
shutil.move(os.path.join(new_project_dir, "LibGdxDesktopProject"), 
            os.path.join(new_project_dir, project_name + "DesktopProject"))
shutil.move(os.path.join(new_project_dir, "LibGdxCoreProject"), 
            os.path.join(new_project_dir, project_name + "CoreProject"))    

print ("New project created at: " + new_project_dir)