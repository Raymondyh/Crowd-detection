<template>
	<div class="system-predict-container layout-padding">
		<div class="system-predict-padding layout-padding-auto layout-padding-view">
			<div class="header">
				<div>
					<el-select v-model="model" placeholder="请选择模型" size="large" style="width: 240px">
						<el-option v-for="item in state.model_items" :key="item.value" :label="item.label"
							:value="item.value" />
					</el-select>
				</div>
				<div style="margin-left: 20px">
					<el-select v-model="height" placeholder="请设置高度" size="large" style="width: 240px">
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
const height = ref('');
const { userInfos } = storeToRefs(stores);

const handleAvatarSuccessone: UploadProps['onSuccess'] = (response, uploadFile) => {
	ElMessage.success('上传成功！');
	state.form.video = response.data;
};
const state = reactive({
	model_items: [] as any,
	height_item: [] as any,
	img_path: '',
	type_text:"正在保存",
	percentage: 50,
	isShow: false,
	form: {
		username: '',
		height: '',
		model: '',
		time: '' as any,
		video: null as any
	},
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
			state.height_item = generateRangeItem()
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

const upData = () => {
	state.form.height = height.value;
	state.form.model = model.value;
	state.form.username = userInfos.value.userName;
	state.form.time = formatDate(new Date(), 'YYYY-mm-dd HH:MM:SS');
	console.log(state.form);
	const queryParams = new URLSearchParams(state.form).toString();
	state.img_path = `http://127.0.0.1:5000/person_count?${queryParams}`;
	ElMessage.success('正在加载！');
};

onMounted(() => {
	// state.height_item =  generateRangeItem()
	getData();
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