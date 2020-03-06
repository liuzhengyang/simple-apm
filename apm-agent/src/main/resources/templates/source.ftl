<!DOCTYPE html>
<html xmlns:th="http://www.thymeleaf.org">
<head>
    <title>ACE in Action</title>
    <link rel="stylesheet" href="https://cdn.jsdelivr.net/npm/xterm@4.4.0/css/xterm.css" />
    <style type="text/css" media="screen">
        #editor {
            float: left;
            width: 1000px;
            height: 2000px;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            left: 0;
        }
        #terminal {
            margin-left: 1010px;
            height: 2000px;
            position: absolute;
            top: 0;
            right: 0;
            bottom: 0;
            /*left: 0;*/
        }
    </style>
</head>
<body>

<div id="editor">${content}</div>
<div id="terminal">

</div>
<!--<script src="/ace-builds/src-noconflict/ace.js" type="text/javascript" charset="utf-8"></script>-->
<script src="https://cdnjs.cloudflare.com/ajax/libs/ace/1.4.7/ace.js" integrity="sha256-C7DTYRJLG+B/VEzHGeoPMw699nsTQYPAXHKXZb+q04E=" crossorigin="anonymous"></script>
<script src="https://code.jquery.com/jquery-1.11.2.min.js"></script>
<script src="//cdn.jsdelivr.net/sockjs/0.3.4/sockjs.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/xterm@4.4.0/lib/xterm.min.js"></script>
<script src="/static/js/vertx-eventbus.js"></script>
<script>
    var editor = ace.edit("editor");
    editor.setTheme("ace/theme/monokai");
    editor.session.setMode("ace/mode/java");
</script>
<script>
    var term = new Terminal();
    term.open(document.getElementById('terminal'));
    term.writeln('\x1B[1;3;31msimple-apm\x1B[0m $ ')

    var eb = new EventBus("http://localhost:8080/eventbus/");
    eb.onopen = function () {
        eb.registerHandler("chat.to.client", function (err, msg) {
            $('#debugger').append(msg.body + "\n");
            term.writeln(msg.body)
        });
    };

    function send(event) {
        if (event.keyCode == 13 || event.which == 13) {
            var message = $('#input').val();
            if (message.length > 0) {
                eb.publish("chat.to.server", message);
                $('#input').val("");
            }
        }
    }
</script>
</body>
</html>
