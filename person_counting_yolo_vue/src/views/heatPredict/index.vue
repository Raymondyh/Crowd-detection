<template>
	<div class="system-predict-container layout-padding">
		<div class="system-predict-padding layout-padding-auto layout-padding-view">
			<div class="header">
				<div>
					<el-select v-model="show" placeholder="请选择是否计数" size="large" style="width: 240px" @change="handleChange">
						<el-option v-for="item in state.show_items" :key="item.value" :label="item.label" :value="item.value"/>
					</el-select>
				</div>
				<div>
					<el-select v-model="model" placeholder="请选择模型" size="large" style="width: 240px; margin-left: 20px;">
						<el-option v-for="item in state.model_items" :key="item.value" :label="item.label" :value="item.value"/>
					</el-select>
				</div>

				<div>
					<el-select v-model="height" v-show="state.heightShow" placeholder="请选择高度" size="large" style="width: 240px; margin-left: 20px;">
						<el-option v-for="item in state.height_item" :key="item.value" :label="item.label"
							:value="item.value" />
					</el-select>
				</div>

				<el-upload v-model="state.form.video" ref="uploadFile" class="avatar-uploader"
					action="http://localhost:9999/files/upload" :show-file-list="false"
					:on-success="handleAvatarSuccessone">
					<div class="button-section" style="margin-left: 20px">
						<el-button type="info" class="predict-button">上传视频</el-button>
					</div>
				</el-upload>
				<div class="button-section" style="margin-left: 20px">
					<el-button type="primary" @click="upData" class="predict-button">开始处理</el-button>
				</div>
				<div class="demo-progress" v-if="state.isShow">
					<el-progress :text-inside="true" :stroke-width="20" :percentage=state.percentage>
						<span>{{state.type_text}} {{state.percentage}}%</span>
					</el-progress>
				</div>
			</div>
			<div class="cards" ref="cardsContainer">
				<img v-if="state.img_path" class="video" :src="state.img_path">
			</div>
		</div>
	</div>
</template>


<script setup lang="ts">
import { reactive, ref, onMounted } from 'vue';
import { ElMessage } from 'element-plus';
import request from '/@/utils/request';
import { useUserInfo } from '/@/stores/userInfo';
import { storeToRefs } from 'pinia';
import type { UploadInstance, UploadProps } from 'element-plus';
import { SocketService } from '/@/utils/socket';
import { formatDate } from '/@/utils/formatTime';

const uploadFile = ref<UploadInstance>();
const stores = useUserInfo();
const model = ref('');
const show = ref('') as any;
const height = ref('');
const { userInfos } = storeToRefs(stores);

const handleAvatarSuccessone: UploadProps['onSuccess'] = (response, uploadFile) => {
	ElMessage.success('上传成功！');
	state.form.video = response.data;
};
const state = reactive({
	model_items: [] as any,
	show_items:[{
            value: true,
            label: '添加计数',
          },
          {
            value: false,
            label: '不计数',
          }],
	height_item: [] as any,
	img_path: '',
	type_text:"正在保存",
	percentage: 50,
	heightShow: false,
	isShow: false,
	form: {
		username: '',
		height: '',
		model: '',
		time: '' as any,
		video: null as any,
		show_in: false,
		show_out: false
	} as any,
});

const socketService = new SocketService();

socketService.on('message', (data) => {
	console.log('Received message:', data);
	ElMessage.success(data);
});

socketService.on('progress', (data) => {
	state.percentage = parseInt(data);
	if ( parseInt(data) < 100 ) {
	    state.isShow = true;
	}else {
		//两秒后隐藏进度条
		ElMessage.success("保存成功！");
		setTimeout(() => {
			state.isShow = false;
			state.percentage = 0;
		}, 2000);
	}
	console.log('Received message:', data);
});

const getData = () => {
	request.get('/api/flask/file_names').then((res) => {
		if (res.code == 0) {
			res.data = JSON.parse(res.data);
			console.log(res.data);
			state.model_items = res.data.model_items;
		} else {
			ElMessage.error(res.msg);
		}
	});
};
const generateRangeItem = (): { value: string, label: string }[] => {
	return Array.from({ length: 22 }, (_, i) => {
		const num = i * 50 + 1;
		return { value: num.toString(), label: '设置高度为：' + num.toString() + ' px' };
	});
}

const handleChange = () => {
	state.heightShow = show.value;
}

const upData = () => {
	if (show.value) {
	    state.form.height = height.value;
		state.form.show_in = 'True';
		state.form.show_out = 'True';
	} else {
		state.form.height = 500;
		state.form.show_in = 'False';
		state.form.show_out = 'False';
	}
	state.form.model = model.value;
	state.form.username = userInfos.value.userName;
	state.form.time = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS');
	const queryParams = new URLSearchParams(state.form).toString();
	state.img_path = `http://127.0.0.1:5000/heat_count?${queryParams}`;
	ElMessage.success('正在加载！');
};

onMounted(() => {
	getData();
	state.height_item = generateRangeItem()
});
</script>

<style scoped lang="scss">
.system-predict-container {
	width: 100%;
	height: 100%;
	display: flex;
	flex-direction: column;
	// background: radial-gradient(circle, #d3e3f1 0%, #ffffff 100%);

	.system-predict-padding {
		padding: 15px;
		background: radial-gradient(circle, #d3e3f1 0%, #ffffff 100%);

		.el-table {
			flex: 1;
		}
	}
}

.header {
	width: 100%;
	height: 5%;
	display: flex;
	justify-content: start;
	align-items: center;
	font-size: 20px;
}

.cards {
	width: 100%;
	height: 95%;
	border-radius: 5px;
	margin-top: 15px;
	padding: 0px;
	overflow: hidden;
	display: flex;
	justify-content: center;
	align-items: center;
	background: radial-gradient(circle, #d3e3f1 0%, #ffffff 100%);
	/* 防止视频溢出 */
}

.video {
	width: 100%;
	max-height: 100%;
	/* 限制视频最大高度不超过父元素高度 */
	height: auto;
	object-fit: contain;
}

.button-section {
	display: flex;
	justify-content: center;
}

.predict-button {
	width: 100%;
	/* 按钮宽度填满 */
}

.demo-progress .el-progress--line {
	margin-left: 20px;
	width: 600px;
}
</style>