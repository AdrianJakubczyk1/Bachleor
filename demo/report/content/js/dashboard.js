/*
   Licensed to the Apache Software Foundation (ASF) under one or more
   contributor license agreements.  See the NOTICE file distributed with
   this work for additional information regarding copyright ownership.
   The ASF licenses this file to You under the Apache License, Version 2.0
   (the "License"); you may not use this file except in compliance with
   the License.  You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
var showControllersOnly = false;
var seriesFilter = "";
var filtersOnlySampleSeries = true;

/*
 * Add header in statistics table to group metrics by category
 * format
 *
 */
function summaryTableHeader(header) {
    var newRow = header.insertRow(-1);
    newRow.className = "tablesorter-no-sort";
    var cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Requests";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 3;
    cell.innerHTML = "Executions";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 7;
    cell.innerHTML = "Response Times (ms)";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 1;
    cell.innerHTML = "Throughput";
    newRow.appendChild(cell);

    cell = document.createElement('th');
    cell.setAttribute("data-sorter", false);
    cell.colSpan = 2;
    cell.innerHTML = "Network (KB/sec)";
    newRow.appendChild(cell);
}

/*
 * Populates the table identified by id parameter with the specified data and
 * format
 *
 */
function createTable(table, info, formatter, defaultSorts, seriesIndex, headerCreator) {
    var tableRef = table[0];

    // Create header and populate it with data.titles array
    var header = tableRef.createTHead();

    // Call callback is available
    if(headerCreator) {
        headerCreator(header);
    }

    var newRow = header.insertRow(-1);
    for (var index = 0; index < info.titles.length; index++) {
        var cell = document.createElement('th');
        cell.innerHTML = info.titles[index];
        newRow.appendChild(cell);
    }

    var tBody;

    // Create overall body if defined
    if(info.overall){
        tBody = document.createElement('tbody');
        tBody.className = "tablesorter-no-sort";
        tableRef.appendChild(tBody);
        var newRow = tBody.insertRow(-1);
        var data = info.overall.data;
        for(var index=0;index < data.length; index++){
            var cell = newRow.insertCell(-1);
            cell.innerHTML = formatter ? formatter(index, data[index]): data[index];
        }
    }

    // Create regular body
    tBody = document.createElement('tbody');
    tableRef.appendChild(tBody);

    var regexp;
    if(seriesFilter) {
        regexp = new RegExp(seriesFilter, 'i');
    }
    // Populate body with data.items array
    for(var index=0; index < info.items.length; index++){
        var item = info.items[index];
        if((!regexp || filtersOnlySampleSeries && !info.supportsControllersDiscrimination || regexp.test(item.data[seriesIndex]))
                &&
                (!showControllersOnly || !info.supportsControllersDiscrimination || item.isController)){
            if(item.data.length > 0) {
                var newRow = tBody.insertRow(-1);
                for(var col=0; col < item.data.length; col++){
                    var cell = newRow.insertCell(-1);
                    cell.innerHTML = formatter ? formatter(col, item.data[col]) : item.data[col];
                }
            }
        }
    }

    // Add support of columns sort
    table.tablesorter({sortList : defaultSorts});
}

$(document).ready(function() {

    // Customize table sorter default options
    $.extend( $.tablesorter.defaults, {
        theme: 'blue',
        cssInfoBlock: "tablesorter-no-sort",
        widthFixed: true,
        widgets: ['zebra']
    });

    var data = {"OkPercent": 44.506322180125075, "KoPercent": 55.493677819874925};
    var dataset = [
        {
            "label" : "FAIL",
            "data" : data.KoPercent,
            "color" : "#FF6347"
        },
        {
            "label" : "PASS",
            "data" : data.OkPercent,
            "color" : "#9ACD32"
        }];
    $.plot($("#flot-requests-summary"), dataset, {
        series : {
            pie : {
                show : true,
                radius : 1,
                label : {
                    show : true,
                    radius : 3 / 4,
                    formatter : function(label, series) {
                        return '<div style="font-size:8pt;text-align:center;padding:2px;color:white;">'
                            + label
                            + '<br/>'
                            + Math.round10(series.percent, -2)
                            + '%</div>';
                    },
                    background : {
                        opacity : 0.5,
                        color : '#000'
                    }
                }
            }
        },
        legend : {
            show : true
        }
    });

    // Creates APDEX table
    createTable($("#apdexTable"), {"supportsControllersDiscrimination": true, "overall": {"data": [0.44506322180125074, 500, 1500, "Total"], "isController": false}, "titles": ["Apdex", "T (Toleration threshold)", "F (Frustration threshold)", "Label"], "items": [{"data": [1.0, 500, 1500, "POST /perform_login (teacher9)"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/lessons/new (LESSON_ID)"], "isController": false}, {"data": [1.0, 500, 1500, "AUX GET /login"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/lessons/${LESSON_ID}/edit"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher10)"], "isController": false}, {"data": [0.0, 500, 1500, "POST /admin/posts/${ADMIN_POST_ID}/delete"], "isController": false}, {"data": [1.0, 500, 1500, "AUX GET /admin/posts/new"], "isController": false}, {"data": [0.0, 500, 1500, "AUX GET /teacher/lessons/new"], "isController": false}, {"data": [0.0, 500, 1500, "AUX GET /teacher/lessons/${LESSON_ID}/tasks/new"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher1)"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/posts/${TEACH_POST_ID}/delete"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher3)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher5)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher7)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher8)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /admin/posts/new (ADMIN_POST_ID)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (admin/password)"], "isController": false}, {"data": [0.0, 500, 1500, "POST /admin/posts/${ADMIN_POST_ID}/edit"], "isController": false}, {"data": [0.0, 500, 1500, "AUX GET /teacher/lessons/${LESSON_ID}/edit"], "isController": false}, {"data": [1.0, 500, 1500, "AUX GET /teacher/posts/new"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/posts/${TEACH_POST_ID}/edit"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/lessons/${LESSON_ID}/delete"], "isController": false}, {"data": [0.0, 500, 1500, "AUX GET /admin/posts/${ADMIN_POST_ID}/edit"], "isController": false}, {"data": [0.0, 500, 1500, "AUX GET /teacher/posts/${TEACH_POST_ID}/edit"], "isController": false}, {"data": [1.0, 500, 1500, "POST /teacher/posts/new (TEACH_POST_ID)"], "isController": false}, {"data": [1.0, 500, 1500, "AUX GET /admin/posts/new-0"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/tasks/${TASK_ID}/delete"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher2)"], "isController": false}, {"data": [1.0, 500, 1500, "AUX GET /admin/posts/new-1"], "isController": false}, {"data": [0.0, 500, 1500, "POST /teacher/lessons/${LESSON_ID}/tasks/new (TASK_ID)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher4)"], "isController": false}, {"data": [1.0, 500, 1500, "POST /perform_login (teacher6)"], "isController": false}]}, function(index, item){
        switch(index){
            case 0:
                item = item.toFixed(3);
                break;
            case 1:
            case 2:
                item = formatDuration(item);
                break;
        }
        return item;
    }, [[0, 0]], 3);

    // Create statistics table
    createTable($("#statisticsTable"), {"supportsControllersDiscrimination": true, "overall": {"data": ["Total", 21907, 12157, 55.493677819874925, 7.443876386543088, 0, 89, 1.0, 7.0, 68.0, 73.0, 36.54156985132834, 45.94683485256268, 6.443231695645936], "isController": false}, "titles": ["Label", "#Samples", "FAIL", "Error %", "Average", "Min", "Max", "Median", "90th pct", "95th pct", "99th pct", "Transactions/s", "Received", "Sent"], "items": [{"data": ["POST /perform_login (teacher9)", 75, 0, 0.0, 69.36, 65, 76, 69.0, 72.4, 75.0, 76.0, 0.13429308122045552, 0.054687709832939406, 0.0603269700795015], "isController": false}, {"data": ["POST /teacher/lessons/new (LESSON_ID)", 811, 811, 100.0, 4.0887792848335405, 2, 16, 4.0, 6.0, 7.0, 10.0, 1.361126495812564, 0.8108273070758437, 0.5223854617718141], "isController": false}, {"data": ["AUX GET /login", 1900, 0, 0.0, 3.072631578947375, 1, 69, 3.0, 5.0, 6.0, 8.0, 3.1702543378256394, 6.259078633361754, 0.7044788758862112], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/edit", 810, 810, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3639922875497816, 1.6077526280005725, 0.0], "isController": false}, {"data": ["POST /perform_login (teacher10)", 86, 0, 0.0, 69.31395348837209, 65, 78, 68.0, 73.3, 75.64999999999999, 78.0, 0.15359117600834393, 0.06254640663621037, 0.0691460274803189], "isController": false}, {"data": ["POST /admin/posts/${ADMIN_POST_ID}/delete", 1078, 1078, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.8076087248142088, 2.134178660449588, 0.0], "isController": false}, {"data": ["AUX GET /admin/posts/new", 1081, 0, 0.0, 4.612395929694728, 2, 22, 4.0, 7.0, 8.899999999999864, 12.180000000000064, 1.8066590678909387, 4.107326474658306, 0.822171021130056], "isController": false}, {"data": ["AUX GET /teacher/lessons/new", 811, 811, 100.0, 4.636251541307025, 2, 13, 4.0, 7.0, 8.0, 10.0, 1.36067433073614, 0.9288196847505485, 0.3215656133185018], "isController": false}, {"data": ["AUX GET /teacher/lessons/${LESSON_ID}/tasks/new", 810, 810, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3640014751423362, 1.6144236209692493, 0.0], "isController": false}, {"data": ["POST /perform_login (teacher1)", 62, 0, 0.0, 69.11290322580646, 65, 77, 68.5, 72.0, 74.85, 77.0, 0.1108106514057761, 0.04512504066035999, 0.049778222311188475], "isController": false}, {"data": ["POST /teacher/posts/${TEACH_POST_ID}/delete", 811, 811, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3611333491097248, 1.6096996931365983, 0.0], "isController": false}, {"data": ["POST /perform_login (teacher3)", 87, 0, 0.0, 69.12643678160919, 66, 84, 68.0, 73.0, 74.0, 84.0, 0.14967870691360785, 0.06095314529587351, 0.06723848162134728], "isController": false}, {"data": ["POST /perform_login (teacher5)", 92, 0, 0.0, 69.8369565217391, 65, 81, 70.0, 73.7, 75.35, 81.0, 0.16570157506551514, 0.06747808281476544, 0.07443625442396189], "isController": false}, {"data": ["POST /perform_login (teacher7)", 75, 0, 0.0, 69.61333333333332, 66, 79, 69.0, 74.0, 77.2, 79.0, 0.13571786608084804, 0.055267920073939096, 0.06096701015350596], "isController": false}, {"data": ["POST /perform_login (teacher8)", 74, 0, 0.0, 68.93243243243246, 65, 84, 69.0, 72.0, 73.0, 84.0, 0.1262309651380779, 0.05140460201423681, 0.05670531637062094], "isController": false}, {"data": ["POST /admin/posts/new (ADMIN_POST_ID)", 1080, 0, 0.0, 2.5555555555555554, 1, 9, 2.0, 4.0, 5.0, 7.0, 1.8082516550525565, 0.5397040604667633, 0.8688084123885331], "isController": false}, {"data": ["POST /perform_login (admin/password)", 1081, 0, 0.0, 69.43108233117466, 65, 89, 69.0, 73.0, 74.0, 79.0, 1.806221354849813, 0.5609163973068755, 0.8060968351234029], "isController": false}, {"data": ["POST /admin/posts/${ADMIN_POST_ID}/edit", 1080, 1080, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.807961055179641, 2.131063470314284, 0.0], "isController": false}, {"data": ["AUX GET /teacher/lessons/${LESSON_ID}/edit", 810, 810, 100.0, 0.0012345679012345698, 0, 1, 0.0, 0.0, 0.0, 0.0, 1.3637052376382646, 1.607414279130259, 0.0], "isController": false}, {"data": ["AUX GET /teacher/posts/new", 815, 0, 0.0, 3.6981595092024553, 1, 25, 3.0, 6.0, 7.0, 9.0, 1.3635740027505714, 6.939047000325417, 0.3195876568946652], "isController": false}, {"data": ["POST /teacher/posts/${TEACH_POST_ID}/edit", 812, 812, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3602251407129458, 1.6059689405487805, 0.0], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/delete", 810, 810, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3640428208405535, 1.6104763382775675, 0.0], "isController": false}, {"data": ["AUX GET /admin/posts/${ADMIN_POST_ID}/edit", 1080, 1080, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.8080972622393954, 2.1312240190653813, 0.0], "isController": false}, {"data": ["AUX GET /teacher/posts/${TEACH_POST_ID}/edit", 814, 814, 100.0, 0.0012285012285012296, 0, 1, 0.0, 0.0, 0.0, 0.0, 1.3631758312609377, 1.609452714838353, 0.0], "isController": false}, {"data": ["POST /teacher/posts/new (TEACH_POST_ID)", 815, 0, 0.0, 3.424539877300614, 1, 19, 3.0, 5.0, 6.0, 9.0, 1.3636880818681356, 0.4527870584327794, 0.6605364146548781], "isController": false}, {"data": ["AUX GET /admin/posts/new-0", 1081, 0, 0.0, 2.5559666975023108, 1, 12, 2.0, 4.0, 5.0, 8.0, 1.806668126262871, 0.5416475730104505, 0.4199091934087532], "isController": false}, {"data": ["POST /teacher/tasks/${TASK_ID}/delete", 810, 810, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.36433460895812, 1.6054914099556001, 0.0], "isController": false}, {"data": ["POST /perform_login (teacher2)", 96, 0, 0.0, 69.80208333333333, 66, 82, 69.0, 74.0, 78.14999999999999, 82.0, 0.16532455276264216, 0.06732454931838064, 0.07426688893634316], "isController": false}, {"data": ["AUX GET /admin/posts/new-1", 1081, 0, 0.0, 1.975948196114707, 1, 15, 2.0, 3.0, 4.0, 6.0, 1.8066741652279568, 3.565711413989942, 0.40226729460153726], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/tasks/new (TASK_ID)", 810, 810, 100.0, 0.0, 0, 0, 0.0, 0.0, 0.0, 0.0, 1.3639325712149608, 1.6143420667114576, 0.0], "isController": false}, {"data": ["POST /perform_login (teacher4)", 82, 0, 0.0, 69.34146341463418, 65, 79, 69.0, 73.7, 75.85, 79.0, 0.13696868261182574, 0.055777285790167316, 0.06152890039203109], "isController": false}, {"data": ["POST /perform_login (teacher6)", 87, 0, 0.0, 69.60919540229887, 66, 85, 69.0, 73.0, 75.0, 85.0, 0.14965424708432243, 0.06094318460367427, 0.06722749380741046], "isController": false}]}, function(index, item){
        switch(index){
            // Errors pct
            case 3:
                item = item.toFixed(2) + '%';
                break;
            // Mean
            case 4:
            // Mean
            case 7:
            // Median
            case 8:
            // Percentile 1
            case 9:
            // Percentile 2
            case 10:
            // Percentile 3
            case 11:
            // Throughput
            case 12:
            // Kbytes/s
            case 13:
            // Sent Kbytes/s
                item = item.toFixed(2);
                break;
        }
        return item;
    }, [[0, 0]], 0, summaryTableHeader);

    // Create error table
    createTable($("#errorsTable"), {"supportsControllersDiscrimination": false, "titles": ["Type of error", "Number of errors", "% in errors", "% in all samples"], "items": [{"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/tasks/new", 1620, 13.325656000658057, 7.394896608390012], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/edit", 1620, 13.325656000658057, 7.394896608390012], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/tasks/${TASK_ID}/delete", 810, 6.6628280003290286, 3.697448304195006], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/delete", 1078, 8.867319239944065, 4.9208015702743415], "isController": false}, {"data": ["403", 811, 6.671053713909681, 3.7020130551878396], "isController": false}, {"data": ["404", 811, 6.671053713909681, 3.7020130551878396], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/delete", 811, 6.671053713909681, 3.7020130551878396], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/delete", 810, 6.6628280003290286, 3.697448304195006], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/edit", 2160, 17.767541334210744, 9.859862144520017], "isController": false}, {"data": ["Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/edit", 1626, 13.375010282141975, 7.422285114347012], "isController": false}]}, function(index, item){
        switch(index){
            case 2:
            case 3:
                item = item.toFixed(2) + '%';
                break;
        }
        return item;
    }, [[1, 1]]);

        // Create top5 errors by sampler
    createTable($("#top5ErrorsBySamplerTable"), {"supportsControllersDiscrimination": false, "overall": {"data": ["Total", 21907, 12157, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/edit", 2160, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/edit", 1626, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/tasks/new", 1620, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/edit", 1620, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/delete", 1078], "isController": false}, "titles": ["Sample", "#Samples", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors", "Error", "#Errors"], "items": [{"data": [], "isController": false}, {"data": ["POST /teacher/lessons/new (LESSON_ID)", 811, 811, "403", 811, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/edit", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/edit", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /admin/posts/${ADMIN_POST_ID}/delete", 1078, 1078, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/delete", 1078, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["AUX GET /teacher/lessons/new", 811, 811, "404", 811, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["AUX GET /teacher/lessons/${LESSON_ID}/tasks/new", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/tasks/new", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /teacher/posts/${TEACH_POST_ID}/delete", 811, 811, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/delete", 811, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /admin/posts/${ADMIN_POST_ID}/edit", 1080, 1080, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/edit", 1080, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["AUX GET /teacher/lessons/${LESSON_ID}/edit", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/edit", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /teacher/posts/${TEACH_POST_ID}/edit", 812, 812, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/edit", 812, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/delete", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/delete", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["AUX GET /admin/posts/${ADMIN_POST_ID}/edit", 1080, 1080, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 35: http://localhost:8080/admin/posts/${ADMIN_POST_ID}/edit", 1080, "", "", "", "", "", "", "", ""], "isController": false}, {"data": ["AUX GET /teacher/posts/${TEACH_POST_ID}/edit", 814, 814, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/posts/${TEACH_POST_ID}/edit", 814, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /teacher/tasks/${TASK_ID}/delete", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 37: http://localhost:8080/teacher/tasks/${TASK_ID}/delete", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}, {"data": ["POST /teacher/lessons/${LESSON_ID}/tasks/new (TASK_ID)", 810, 810, "Non HTTP response code: java.net.URISyntaxException/Non HTTP response message: Illegal character in path at index 39: http://localhost:8080/teacher/lessons/${LESSON_ID}/tasks/new", 810, "", "", "", "", "", "", "", ""], "isController": false}, {"data": [], "isController": false}, {"data": [], "isController": false}]}, function(index, item){
        return item;
    }, [[0, 0]], 0);

});
