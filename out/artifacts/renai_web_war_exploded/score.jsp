<%--
  Created by IntelliJ IDEA.
  User: sun
  Date: 2023/8/10
  Time: 15:26
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<!--
此示例下载自 https://echarts.apache.org/examples/zh/editor.html?c=line-simple&lang=js&code=PYBwLglsB2AEC8sDeAoWsAeBBDEDOAXMmurGAJ4gCmRA5AMYCGYVA5sAE7m0A0J6AE2aMiAbVoBZGL1i0AKgFcqM2gHUqAlXIAWClQDEOEFQGVmphdFoBdEgF8-6cjnxFUpMpRqyAbowA2SrT2jrB4VEZUhLCi_MQegsJiAIwArAAM6TywAEwAzFm5OQAs2TnJABzZyXmp1cUA7GUAbOnWoR4U1HT-ENDKcXYktnYA3EA
⚠ 请注意，该图表不是 Apache ECharts 官方示例，而是由用户代码生成的。请注意鉴别其内容。
-->
<!DOCTYPE html>
<html lang="zh-CN" style="height: 100%">
<head>
    <meta charset="utf-8">
</head>
<body style="height: 100%; margin: 0">
<div id="container" style="height: 100%"></div>


<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="${pageContext.request.contextPath}/js/echarts.min.js"></script>
<!-- Uncomment this line if you want to dataTool extension
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@5.4.3/dist/extension/dataTool.min.js"></script>
-->
<!-- Uncomment this line if you want to use gl extension
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts-gl@2/dist/echarts-gl.min.js"></script>
-->
<!-- Uncomment this line if you want to echarts-stat extension
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts-stat@latest/dist/ecStat.min.js"></script>
-->
<!-- Uncomment this line if you want to use map
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@4.9.0/map/js/china.js"></script>
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@4.9.0/map/js/world.js"></script>
-->
<!-- Uncomment these two lines if you want to use bmap extension
<script type="text/javascript" src="https://api.map.baidu.com/api?v=3.0&ak=YOUR_API_KEY"></script>
<script type="text/javascript" src="https://fastly.jsdelivr.net/npm/echarts@5.4.3/dist/extension/bmap.min.js"></script>
-->

<script type="text/javascript">
    $(function () {
        $.get("${pageContext.request.contextPath}/scoreMin,/score",function (data,data1) {
            var Arr1 = new Array();
            var Arr2 = new Array();
            var Arr3 = new Array();
            console.log(data);
            console.log(data1);
            for (i = 0;i<data.length;i++){
                Arr1.push(data[i].year)
                Arr2.push(data[i].min)
                // console.log(Arr1);
                // console.log(Arr2);
            }
            for (j = 0;j<data1.length;j++){
                Arr1.push(data[j].year)
                Arr3.push(data[j].score)
                // console.log(Arr1);
                // console.log(Arr3);
            }
            var dom = document.getElementById('container');
            var myChart = echarts.init(dom, null, {
                renderer: 'canvas',
                useDirtyRect: false
            });
            var app = {};

            var option;
            const colors = ['#5470C6', '#EE6666'];
            option = {
                color: colors,
                tooltip: {
                    trigger: 'none',
                    axisPointer: {
                        type: 'cross'
                    }
                },
                legend: {},
                grid: {
                    top: 70,
                    bottom: 50
                },
                xAxis: [
                    {
                        type: 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        axisLine: {
                            onZero: false,
                            lineStyle: {
                                color: colors[1]
                            }
                        },
                        axisPointer: {
                            label: {
                                formatter: function (params) {
                                    return (
                                        'Precipitation  ' +
                                        params.value +
                                        (params.seriesData.length ? '：' + params.seriesData[0].data : '')
                                    );
                                }
                            }
                        },
                        // prettier-ignore
                        data: Arr1
                    },
                    {
                        type: 'category',
                        axisTick: {
                            alignWithLabel: true
                        },
                        axisLine: {
                            onZero: false,
                            lineStyle: {
                                color: colors[0]
                            }
                        },
                        axisPointer: {
                            label: {
                                formatter: function (params) {
                                    return (
                                        'Precipitation  ' +
                                        params.value +
                                        (params.seriesData.length ? '：' + params.seriesData[0].data : '')
                                    );
                                }
                            }
                        },
                        // prettier-ignore
                        data1: Arr1
                    }
                ],
                yAxis: [
                    {
                        type: 'value'
                    }
                ],
                series: [
                    {
                        name: 'min',
                        type: 'line',
                        xAxisIndex: 1,
                        smooth: true,
                        emphasis: {
                            focus: 'series'
                        },
                        data:Arr2
                    },
                    {
                        name: 'max',
                        type: 'line',
                        smooth: true,
                        emphasis: {
                            focus: 'series'
                        },
                        data1: Arr3
                    }
                ]
            };

            if (option && typeof option === 'object') {
                myChart.setOption(option);
            }

            window.addEventListener('resize', myChart.resize);
        },"json")
    })

</script>
</body>
</html>
