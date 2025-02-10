import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import org.yazeez.layeredpackage.CreateFilesDialog

class GenerateLayeredPackageAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val baseDir = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE) ?: return

        // Ask the user for a feature name
        val dialog = CreateFilesDialog()

        if (dialog.showAndGet()) {
            val featureName = dialog.getFeatureName()
            val createProviderLayer = dialog.shouldCreateProviderLayer()

            // Convert feature name to PascalCase for class names
            val fileName = featureName
//            val pascalCaseName = featureName.replaceFirstChar { it.uppercase() }
            val camelCaseName = featureName.split('_').joinToString("", transform = String::capitalize)

            WriteCommandAction.runWriteCommandAction(project) {
                // Create directories
                val dataDir = baseDir.createChildDirectory(this, "data")
                val repoDir = baseDir.createChildDirectory(this, "repo")
//            val domainDir = baseDir.createChildDirectory(this, "domain")

                // data layer
                dataDir.createChildData(this, "base_${fileName}_data.dart").setBinaryContent(
                    """
                    abstract class Base${camelCaseName}Data {
                    
                    }
                    """.trimIndent().toByteArray()
                )

                dataDir.createChildData(this, "${fileName}_data_impl.dart").setBinaryContent(
                    """
                    import '../../../core/network/http_client.dart';   
                    import 'base_${fileName}_data.dart';

                    class ${camelCaseName}DataImpl implements Base${camelCaseName}Data {
                       final HttpClient _httpClient;
                       ${camelCaseName}DataImpl(this._httpClient);
                        
                    }
                   """.trimIndent().toByteArray()
                )

                // repo layer
                repoDir.createChildData(this, "base_${fileName}_repo.dart").setBinaryContent(
                    """
                    abstract class Base${camelCaseName}Repo {
                    
                    }
                    """.trimIndent().toByteArray()
                )

                repoDir.createChildData(this, "${fileName}_repo_impl.dart").setBinaryContent(
                    """
                    import 'base_${fileName}_repo.dart';
                    import '../data/base_${fileName}_data.dart';
                    
                    class ${camelCaseName}RepoImpl implements Base${camelCaseName}Repo {
                       final Base${camelCaseName}Data _httpClient;
                       ${camelCaseName}RepoImpl(this._httpClient);
                       
                    }
                    """.trimIndent().toByteArray()
                )

                if (createProviderLayer) {
                    val providerDir = baseDir.createChildDirectory(this, "providers")

                    providerDir.createChildData(this, "${fileName}_provider.dart").setBinaryContent(
                        """
                        import 'package:flutter/material.dart';
                        import '../repo/base_${fileName}_repo.dart';
          
                        class ${camelCaseName}Provider extends ChangeNotifier {
                          final Base${camelCaseName}Repo _${fileName}Repo;
                          ${camelCaseName}Provider(this._${fileName}Repo);
                        
                        }
                        """.trimIndent().toByteArray()
                    )
                }

            }

        }
    }
}