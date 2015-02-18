package org.kaloz.deliverypipeline

import java.io.{File, PrintWriter}

object ExportAppVersion extends App {

  val setup = new PrintWriter(new File("setup.sh"))
  setup.write(s"""#!/bin/bash\rexport APP_VERSION=${BuildInfo.version}""")
  setup.close()
}
