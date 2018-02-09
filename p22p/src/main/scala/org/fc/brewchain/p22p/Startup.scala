package org.fc.brewchain.p22p

import onight.osgi.annotation.NActorProvider
import com.google.protobuf.Message
import onight.oapi.scala.commons.SessionModules
import org.apache.felix.ipojo.annotations.Validate
import org.apache.felix.ipojo.annotations.Invalidate
import org.fc.brewchain.p22p.tasks.LayerNodeTask
import org.fc.brewchain.p22p.node.NodeInstance

@NActorProvider
object Startup extends SessionModules[Message] {

  @Validate
  def init() {

    log.info("startup:" + NodeInstance.curnode.getName);

    LayerNodeTask.initTask();
    log.info("tasks inited....[OK]");
  }

  @Invalidate
  def destory() {

  }
}