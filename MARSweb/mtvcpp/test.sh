#!/bin/sh
start_time='date +%S'
start_time_string='date'

ls -al
date

end_time='date +%S'
end_time_string='date'
elapsed_time='echo "$end_time - $start_time" | bc'
htime='echo "$elapsed_time/3600" |bc'
mtime='echo "($elapsed_time/60) - ($htime * 60)" | bc'
stime='echo "$elapsed_time - (($elapsed_time/60) * 60)" | bc'
echo "=============================================="
echo "Start time : $start_time_string"
echo "End time   : $end_time_string"
echo "Total time : ${htime} H ${mtime} M ${stime} S"
echo "=============================================="

