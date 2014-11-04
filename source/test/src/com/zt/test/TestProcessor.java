package com.zt.test;

import java.io.IOException;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic.Kind;
import javax.tools.JavaFileObject;

@SupportedAnnotationTypes(value= {"com.zt.lib.database.bean.Table"})
@SupportedSourceVersion(SourceVersion.RELEASE_6)
public class TestProcessor extends AbstractProcessor {

	private Filer filer;
	private Messager messager;

	@Override
	public void init(ProcessingEnvironment env) {
		filer = env.getFiler();
		messager = env.getMessager();
	}

	@Override
	public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
		for (Element element : roundEnv.getRootElements()) {

			if (element.getSimpleName().toString().startsWith("Silly")) {
				// 不要循环为已经生成的类生成新的类
				continue;
			}

			if (element.getSimpleName().toString().startsWith("T")) {
				messager.printMessage(Kind.WARNING, "This class name starts with a T!", element);
			}

			String sillyClassName = "Silly" + element.getSimpleName();
			StringBuilder sillyClassContent = new StringBuilder();
			sillyClassContent.append("package silly;\n");
			sillyClassContent.append("public class ").append(sillyClassName).append("{\n");
			for (TypeElement t : annotations) {
				for (Element element2 : roundEnv.getElementsAnnotatedWith(t)) {
					sillyClassContent.append("/*").append(t.getQualifiedName()).append("\n");
					sillyClassContent.append(element2.getSimpleName()).append("*/\n");
				}
			}
			sillyClassContent.append("\n}");

			JavaFileObject file = null;

			try {
				file = filer.createSourceFile("silly/" + sillyClassName, element);
				file.openWriter().append(sillyClassContent).close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		return true;
	}

}
