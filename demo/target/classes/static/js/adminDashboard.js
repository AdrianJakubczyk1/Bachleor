       var labels =  [];
        var values =  [];
        var backgroundColors = [
            'rgba(255, 99, 132, 0.5)',
            'rgba(54, 162, 235, 0.5)',
            'rgba(255, 206, 86, 0.5)',
            'rgba(75, 192, 192, 0.5)',
            'rgba(153, 102, 255, 0.5)',
            'rgba(255, 159, 64, 0.5)'
        ];
        var borderColors = [
            'rgba(255, 99, 132, 1)',
            'rgba(54, 162, 235, 1)',
            'rgba(255, 206, 86, 1)',
            'rgba(75, 192, 192, 1)',
            'rgba(153, 102, 255, 1)',
            'rgba(255, 159, 64, 1)'
        ];
        function getBackgroundColor(index) {
            return backgroundColors[index % backgroundColors.length];
        }
        function getBorderColor(index) {
            return borderColors[index % borderColors.length];
        }

        var barColors = labels.map((_, idx) => getBackgroundColor(idx));
        var barBorders = labels.map((_, idx) => getBorderColor(idx));

        var ctx = document.getElementById('statsChart').getContext('2d');
        var statsChart = new Chart(ctx, {
            type: 'bar',
            data: {
                labels: labels,
                datasets: [{
                    label: 'Application Statistics',
                    data: values,
                    backgroundColor: barColors,
                    borderColor: barBorders,
                    borderWidth: 1
                }]
            },
            options: {
                scales: {
                    x: {
                        ticks: { color: '#fff' }
                    },
                    y: {
                        beginAtZero: true,
                        ticks: { color: '#fff' }
                    }
                },
                plugins: {
                    legend: {
                        labels: {
                            color: '#fff'
                        }
                    }
                }
            }
        });