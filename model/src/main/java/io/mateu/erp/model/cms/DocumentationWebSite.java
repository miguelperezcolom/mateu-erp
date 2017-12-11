package io.mateu.erp.model.cms;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import java.io.File;
import java.util.List;

@Entity
@Getter
@Setter
public class DocumentationWebSite extends Website {
    @Override
    public void addLinesAfterGit(List<String> lines, File where) {

        lines.add("rm -rf build/*");

        lines.add("make html");


        lines.add("rst2pdf source/index.rst build/html/manual.pdf");
        

    }


    /*

    #!/bin/sh


cd /home/doc/quoon-usermanual

git pull

rm -rf build/*

make html

rst2pdf source/index.rst build/html/manual.pdf

     */
}
