import ai.grazie.utils.capitalize
import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.command.WriteCommandAction
import org.yazeez.layeredpackage.CreateFilesDialog
import org.yazeez.layeredpackage.StateManagementType

class GenerateLayeredPackageAction : AnAction() {
    override fun actionPerformed(event: AnActionEvent) {
        val project = event.project ?: return
        val baseDir = event.getData(com.intellij.openapi.actionSystem.CommonDataKeys.VIRTUAL_FILE) ?: return

        // Ask the user for a feature name
        val dialog = CreateFilesDialog()

        if (dialog.showAndGet()) {
            val featureName = dialog.getFeatureName()
            val stateManagementType: StateManagementType = dialog.getSelectedStateManagementType()
            val createUILayer = dialog.shouldCreateUILayer()

            // Convert feature name to PascalCase for class names
            val fileName = featureName
//            val pascalCaseName = featureName.replaceFirstChar { it.uppercase() }
            val camelCaseName = featureName.split('_').joinToString("", transform = { it -> it.capitalize() })
            val objectName = featureName.split('_').joinToString("", transform = { it -> it.capitalize() })
                .replaceFirstChar { it.lowercase() }

            WriteCommandAction.runWriteCommandAction(project) {
                // Create directories
                val dataDir = baseDir.createChildDirectory(this, "data")
                val repoDir = baseDir.createChildDirectory(this, "repo")

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
                       final Base${camelCaseName}Data _${objectName}Data;
                       ${camelCaseName}RepoImpl(this._${objectName}Data);
                       
                    }
                    """.trimIndent().toByteArray()
                )

                if (stateManagementType == StateManagementType.PROVIDER) {
                    val providerDir = baseDir.createChildDirectory(this, "providers")

                    providerDir.createChildData(this, "${fileName}_provider.dart").setBinaryContent(
                        """
                        import 'package:flutter/material.dart';
                        import '../repo/base_${fileName}_repo.dart';
          
                        class ${camelCaseName}Provider extends ChangeNotifier {
                          final Base${camelCaseName}Repo _${objectName}Repo;
                          ${camelCaseName}Provider(this._${objectName}Repo);
                        
                        }
                        """.trimIndent().toByteArray()
                    )
                } else if (stateManagementType == StateManagementType.RIVERPOD) {
                    val providerDir = baseDir.createChildDirectory(this, "riverpods")

                    providerDir.createChildData(this, "${fileName}_riverpod.dart").setBinaryContent(
                        """
                        import 'package:flutter_riverpod/flutter_riverpod.dart';
                        import '../repo/base_${fileName}_repo.dart';
                        
                        final ${objectName}Provider = StateNotifierProvider<${camelCaseName}Provider, dynamic>(
                          ///for better code, use Dependency injection way, such as (riverpod, get_it, ...), instead of [${camelCaseName}RepoImpl()]
                          (ref) => ${camelCaseName}Provider(dynamic, ${camelCaseName}RepoImpl()),
                        );
          
                        class ${camelCaseName}Provider extends StateNotifier<dynamic> {
                          final Base${camelCaseName}Repo _${objectName}Repo;
                          ${camelCaseName}Provider(super.state, this._${objectName}Repo);
                        
                        }
                        """.trimIndent().toByteArray()
                    )
                }

                if (createUILayer) {
                    val providerDir = baseDir.createChildDirectory(this, "ui")

                    providerDir.createChildData(this, "${fileName}_page.dart").setBinaryContent(
                        """
                        import 'package:flutter/material.dart';
          
                        class ${camelCaseName}Page extends StatelessWidget {
                          const ${camelCaseName}Page({super.key});
                        
                          @override
                          Widget build(BuildContext context) {
                          ${if (stateManagementType == StateManagementType.PROVIDER) {
                             """
                             return ChangeNotifierProvider(
                                create: (context) => ${camelCaseName}Provider(${camelCaseName}RepoImpl()),
                                child: const ${camelCaseName}Screen(),
                              );
                            """
                         } else {
                             "return const ${camelCaseName}Screen();"
                         }}
                          }
                        }
                        
                        class ${camelCaseName}Screen extends StatefulWidget {
                          const ${camelCaseName}Screen({super.key});

                          @override
                          State<${camelCaseName}Screen> createState() => _${camelCaseName}ScreenState();
                        }

                        class _${camelCaseName}ScreenState extends State<${camelCaseName}Screen> {
                          @override
                          Widget build(BuildContext context) {
                            return const Placeholder();
                          }
                        }
                        """.trimIndent().toByteArray()
                    )
                }

            }

        }
    }
}