<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>ACE in Action</title>
    <style type="text/css" media="screen">
        #editor {
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
        }
    </style>
</head>
<body>

<div id="editor">${content}</div>
<!--<script src="/ace-builds/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>-->
<script src="https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.7/ace.js" integrity="sha256-C7DTYRJLG+B/VEzHGeoPMw699nsTQYPAXHKXZb+q04E=" crossorigin="anonymous"></script>
<script>
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.session.setMode("ace/mode/java");
</script>
</body>
</html>
