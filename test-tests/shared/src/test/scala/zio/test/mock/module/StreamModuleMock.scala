/*
 * Copyright 2017-2020 John A. De Goes and the ZIO Contributors
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package zio.test.mock.module

import com.github.ghik.silencer.silent

import zio.stream.ZSink
import zio.test.mock.{ Mock, Proxy }
import zio.{ Has, UIO, URLayer, ZLayer }

/**
 * Example module used for testing ZIO Mock framework.
 */
object StreamModuleMock extends Mock[StreamModule] {

  object Sink   extends Sink[Any, String, Nothing, Int, List[Int]]
  object Stream extends Stream[Any, String, Int]

  @silent("is never used")
  val compose: URLayer[Has[Proxy], StreamModule] =
    ZLayer.fromServiceM { proxy =>
      withRuntime.map { rts =>
        new StreamModule.Service {
          def sink(a: Int)   = rts.unsafeRun(proxy(Sink, a).catchAll(error => UIO(ZSink.fail(error))))
          def stream(a: Int) = rts.unsafeRun(proxy(Stream, a))
        }
      }
    }
}
