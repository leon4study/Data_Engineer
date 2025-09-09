#!/bin/bash
# wait-core-nodes.sh
# 모든 Core 노드가 Live 상태가 될 때까지 대기

# Core 노드 수
CORE_NODES=3

echo "Waiting for all Core nodes ($CORE_NODES) to be ready..."

# 최대 대기 시간 (초)
MAX_WAIT=900  # 15분
INTERVAL=10   # 10초마다 체크
WAITED=0

while true; do
    LIVE_NODES=$(hdfs dfsadmin -report | grep 'Live datanodes' | awk '{print $3}')

            if [[ "$LIVE_NODES" -ge "$CORE_NODES" ]]; then
                echo "All Core nodes are ready ($LIVE_NODES/$CORE_NODES)."
                break
            fi

            if [[ $WAITED -ge $MAX_WAIT ]]; then
                echo "Timeout reached waiting for Core nodes. Exiting..."
                exit 1
            fi

            echo "Current live nodes: $LIVE_NODES/$CORE_NODES. Waiting..."
            sleep $INTERVAL
            WAITED=$((WAITED + INTERVAL))
done

